package org.dideng.com.deicore.client.api.config;

import net.fabricmc.loader.api.FabricLoader;
import org.dideng.com.deicore.Deicore;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 配置管理器，负责配置的注册和管理
 */
public class ConfigManager {
    
    private static final ConfigManager INSTANCE = new ConfigManager();
    
    private final Path configDirectory;
    private final Map<String, IConfig> registeredConfigs = new ConcurrentHashMap<>();
    private final Map<String, ConfigRegistration> configRegistrations = new HashMap<>();
    private final ConfigChangeListenerManager listenerManager = new ConfigChangeListenerManager();
    
    private ConfigManager() {
        // 使用FabricLoader获取配置目录
        this.configDirectory = FabricLoader.getInstance().getConfigDir().resolve("deicore");
        Deicore.LOGGER.info("Config directory: {}", configDirectory);
    }
    
    /**
     * 获取配置管理器实例
     */
    public static ConfigManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * 注册配置类
     * @param configId 配置ID
     * @param configClass 配置类
     * @param fileName 配置文件名
     * @param version 配置版本
     * @param <T> 配置类型
     */
    public <T extends IConfig> void registerConfig(String configId, Class<T> configClass, String fileName, int version) {
        configRegistrations.put(configId, new ConfigRegistration(configId, configClass, fileName, version));
        Deicore.LOGGER.debug("Registered config: {} -> {}", configId, fileName);
    }
    
    /**
     * 获取配置实例
     * @param configId 配置ID
     * @param <T> 配置类型
     * @return 配置实例
     */
    @SuppressWarnings("unchecked")
    public <T extends IConfig> T getConfig(String configId) {
        // 如果配置已加载，直接返回
        if (registeredConfigs.containsKey(configId)) {
            return (T) registeredConfigs.get(configId);
        }
        
        // 检查是否已注册
        if (!configRegistrations.containsKey(configId)) {
            Deicore.LOGGER.error("Config not registered: {}", configId);
            return null;
        }
        
        // 创建并加载配置
        ConfigRegistration registration = configRegistrations.get(configId);
        try {
            IConfig config = registration.createInstance(configDirectory);
            
            // 尝试加载配置
            if (config.load()) {
                registeredConfigs.put(configId, config);
                Deicore.LOGGER.info("Config loaded successfully: {}", configId);
            } else {
                Deicore.LOGGER.warn("Failed to load config: {}", configId);
            }
            
            return (T) config;
            
        } catch (Exception e) {
            Deicore.LOGGER.error("Failed to create config instance: {}", configId, e);
            return null;
        }
    }
    
    /**
     * 保存所有配置
     */
    public void saveAllConfigs() {
        int successCount = 0;
        int totalCount = registeredConfigs.size();
        
        for (Map.Entry<String, IConfig> entry : registeredConfigs.entrySet()) {
            if (entry.getValue().save()) {
                successCount++;
            } else {
                Deicore.LOGGER.error("Failed to save config: {}", entry.getKey());
            }
        }
        
        Deicore.LOGGER.info("Saved {}/{} configs", successCount, totalCount);
    }
    
    /**
     * 重新加载所有配置
     */
    public void reloadAllConfigs() {
        for (Map.Entry<String, IConfig> entry : registeredConfigs.entrySet()) {
            if (!entry.getValue().load()) {
                Deicore.LOGGER.error("Failed to reload config: {}", entry.getKey());
            }
        }
        Deicore.LOGGER.info("Reloaded {} configs", registeredConfigs.size());
    }
    
    /**
     * 获取已注册的配置ID列表
     */
    public String[] getRegisteredConfigIds() {
        return configRegistrations.keySet().toArray(new String[0]);
    }
    
    /**
     * 检查配置是否已注册
     */
    public boolean isConfigRegistered(String configId) {
        return configRegistrations.containsKey(configId);
    }
    
    /**
     * 检查配置是否已加载
     */
    public boolean isConfigLoaded(String configId) {
        return registeredConfigs.containsKey(configId);
    }
    
    /**
     * 获取配置目录
     */
    public Path getConfigDirectory() {
        return configDirectory;
    }
    
    /**
     * 添加全局配置变更监听器
     */
    public void addGlobalConfigChangeListener(IConfig.ConfigChangeListener listener) {
        listenerManager.addGlobalListener(listener);
    }
    
    /**
     * 移除全局配置变更监听器
     */
    public void removeGlobalConfigChangeListener(IConfig.ConfigChangeListener listener) {
        listenerManager.removeGlobalListener(listener);
    }
    
    /**
     * 添加特定配置的变更监听器
     */
    public void addConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener) {
        listenerManager.addConfigSpecificListener(configId, listener);
    }
    
    /**
     * 移除特定配置的变更监听器
     */
    public void removeConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener) {
        listenerManager.removeConfigSpecificListener(configId, listener);
    }
    
    /**
     * 获取配置变更监听器管理器
     */
    public ConfigChangeListenerManager getListenerManager() {
        return listenerManager;
    }
    
    /**
     * 配置注册信息
     */
    private static class ConfigRegistration {
        private final String configId;
        private final Class<? extends IConfig> configClass;
        private final String fileName;
        private final int version;
        
        public ConfigRegistration(String configId, Class<? extends IConfig> configClass, String fileName, int version) {
            this.configId = configId;
            this.configClass = configClass;
            this.fileName = fileName;
            this.version = version;
        }
        
        public IConfig createInstance(Path configDirectory) throws Exception {
            return configClass.getDeclaredConstructor(String.class, String.class, Path.class, int.class)
                    .newInstance(configId, fileName, configDirectory, version);
        }
    }
}