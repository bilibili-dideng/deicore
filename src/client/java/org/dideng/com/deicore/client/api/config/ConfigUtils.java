package org.dideng.com.deicore.client.api.config;

import org.dideng.com.deicore.Deicore;

import java.nio.file.Path;

/**
 * 配置工具类，提供便捷的配置操作方法
 */
public class ConfigUtils {
    
    /**
     * 快速注册并获取配置实例
     * @param configId 配置ID
     * @param configClass 配置类
     * @param fileName 配置文件名
     * @param version 配置版本
     * @param <T> 配置类型
     * @return 配置实例
     */
    public static <T extends IConfig> T registerAndGetConfig(String configId, Class<T> configClass, String fileName, int version) {
        ConfigManager manager = ConfigManager.getInstance();
        
        // 注册配置
        manager.registerConfig(configId, configClass, fileName, version);
        
        // 获取配置实例
        T config = manager.getConfig(configId);
        
        if (config != null) {
            Deicore.LOGGER.info("Successfully registered and loaded config: {}", configId);
        } else {
            Deicore.LOGGER.error("Failed to register and load config: {}", configId);
        }
        
        return config;
    }
    
    /**
     * 创建默认的配置实例
     * @param configId 配置ID
     * @param configClass 配置类
     * @param fileName 配置文件名
     * @param version 配置版本
     * @param <T> 配置类型
     * @return 配置实例
     */
    public static <T extends IConfig> T createDefaultConfig(String configId, Class<T> configClass, String fileName, int version) {
        try {
            Path configDirectory = ConfigManager.getInstance().getConfigDirectory();
            return configClass.getDeclaredConstructor(String.class, String.class, Path.class, int.class)
                    .newInstance(configId, fileName, configDirectory, version);
        } catch (Exception e) {
            Deicore.LOGGER.error("Failed to create default config instance: {}", configId, e);
            return null;
        }
    }
    
    /**
     * 检查配置是否有效
     * @param config 配置实例
     * @return 是否有效
     */
    public static boolean isValidConfig(IConfig config) {
        return config != null && config.isLoaded();
    }
    
    /**
     * 安全地获取配置值，如果配置无效则返回默认值
     * @param config 配置实例
     * @param getter 配置值获取函数
     * @param defaultValue 默认值
     * @param <T> 值类型
     * @return 配置值或默认值
     */
    public static <T> T getConfigValueSafe(IConfig config, java.util.function.Supplier<T> getter, T defaultValue) {
        if (isValidConfig(config)) {
            try {
                return getter.get();
            } catch (Exception e) {
                Deicore.LOGGER.warn("Failed to get config value, using default", e);
            }
        }
        return defaultValue;
    }
    
    /**
     * 批量保存配置
     * @param configs 配置实例数组
     */
    @SafeVarargs
    public static <T extends IConfig> void saveConfigs(T... configs) {
        int successCount = 0;
        
        for (T config : configs) {
            if (config != null && config.save()) {
                successCount++;
            }
        }
        
        Deicore.LOGGER.info("Saved {}/{} configs", successCount, configs.length);
    }
    
    /**
     * 批量重新加载配置
     * @param configs 配置实例数组
     */
    @SafeVarargs
    public static <T extends IConfig> void reloadConfigs(T... configs) {
        int successCount = 0;
        
        for (T config : configs) {
            if (config != null && config.load()) {
                successCount++;
            }
        }
        
        Deicore.LOGGER.info("Reloaded {}/{} configs", successCount, configs.length);
    }
    
    /**
     * 添加简单的配置变更监听器
     * @param configId 配置ID
     * @param onChanged 变更回调函数
     */
    public static void addSimpleConfigListener(String configId, java.util.function.Consumer<ConfigChangeEvent> onChanged) {
        ConfigManager.getInstance().addConfigSpecificListener(configId, onChanged);
    }
    
    /**
     * 移除简单的配置变更监听器
     * @param configId 配置ID
     * @param onChanged 变更回调函数
     */
    public static void removeSimpleConfigListener(String configId, java.util.function.Consumer<ConfigChangeEvent> onChanged) {
        ConfigManager.getInstance().removeConfigSpecificListener(configId, onChanged);
    }
}