package org.dideng.com.deicore.client.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dideng.com.deicore.Deicore;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.dideng.com.deicore.client.api.config.ConfigChangeEvent;
import org.dideng.com.deicore.client.api.config.ConfigManager;

/**
 * 抽象配置基类，提供通用的配置功能实现
 */
public abstract class AbstractConfig implements IConfig {
    
    protected static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    
    protected final String configId;
    protected final String configFileName;
    protected final Path configDirectory;
    protected final int version;
    protected boolean loaded = false;
    protected ConfigChangeListener changeListener;
    protected final Map<String, Object> defaultValues = new HashMap<>();
    protected final Map<String, Object> currentValues = new HashMap<>();
    
    /**
     * 构造函数
     * @param configId 配置ID
     * @param configFileName 配置文件名
     * @param configDirectory 配置文件目录
     * @param version 配置版本
     */
    protected AbstractConfig(String configId, String configFileName, Path configDirectory, int version) {
        this.configId = configId;
        this.configFileName = configFileName;
        this.configDirectory = configDirectory;
        this.version = version;
        
        // 初始化默认值
        initializeDefaults();
        // 重置为默认值
        resetToDefaults();
    }
    
    /**
     * 初始化默认值，子类需要实现此方法
     */
    protected abstract void initializeDefaults();
    
    /**
     * 序列化配置到JSON对象
     * @return JSON对象
     */
    protected abstract JsonObject serializeToJson();
    
    /**
     * 从JSON对象反序列化配置
     * @param json JSON对象
     */
    protected abstract void deserializeFromJson(JsonObject json);
    
    @Override
    public String getConfigId() {
        return configId;
    }
    
    @Override
    public String getConfigFileName() {
        return configFileName;
    }
    
    @Override
    public Path getConfigFilePath() {
        return configDirectory.resolve(configFileName);
    }
    
    @Override
    public boolean load() {
        try {
            Path configFile = getConfigFilePath();
            
            // 如果配置文件不存在，创建默认配置
            if (!Files.exists(configFile)) {
                Deicore.LOGGER.info("Config file not found, creating default: {}", configFile);
                return save();
            }
            
            // 读取配置文件
            String jsonContent = Files.readString(configFile);
            JsonObject json = GSON.fromJson(jsonContent, JsonObject.class);
            
            // 检查版本兼容性
            if (!checkVersionCompatibility(json)) {
                Deicore.LOGGER.warn("Config version mismatch for {}, attempting migration", configId);
                if (!migrateConfig(json)) {
                    Deicore.LOGGER.error("Failed to migrate config {}, using defaults", configId);
                    resetToDefaults();
                    return save();
                }
            }
            
            // 反序列化配置
            deserializeFromJson(json);
            loaded = true;
            Deicore.LOGGER.info("Config loaded successfully: {}", configId);
            return true;
            
        } catch (Exception e) {
            Deicore.LOGGER.error("Failed to load config: {}", configId, e);
            resetToDefaults();
            loaded = false;
            return false;
        }
    }
    
    @Override
    public boolean save() {
        try {
            Path configFile = getConfigFilePath();
            
            // 确保目录存在
            Files.createDirectories(configFile.getParent());
            
            // 序列化配置
            JsonObject json = serializeToJson();
            json.addProperty("version", version);
            json.addProperty("configId", configId);
            
            // 写入文件
            String jsonContent = GSON.toJson(json);
            Files.writeString(configFile, jsonContent);
            
            Deicore.LOGGER.debug("Config saved successfully: {}", configId);
            return true;
            
        } catch (Exception e) {
            Deicore.LOGGER.error("Failed to save config: {}", configId, e);
            return false;
        }
    }
    
    @Override
    public void resetToDefaults() {
        currentValues.clear();
        currentValues.putAll(defaultValues);
        
        // 使用反射设置字段的默认值
        try {
            for (Field field : getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigField.class)) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    if (defaultValues.containsKey(fieldName)) {
                        field.set(this, defaultValues.get(fieldName));
                    }
                }
            }
        } catch (Exception e) {
            Deicore.LOGGER.error("Failed to reset config to defaults: {}", configId, e);
        }
    }
    
    @Override
    public boolean isLoaded() {
        return loaded;
    }
    
    @Override
    public int getVersion() {
        return version;
    }
    
    @Override
    public void setChangeListener(ConfigChangeListener listener) {
        this.changeListener = listener;
    }
    
    /**
     * 检查版本兼容性
     * @param json JSON配置
     * @return 是否兼容
     */
    protected boolean checkVersionCompatibility(JsonObject json) {
        if (!json.has("version")) {
            return false; // 旧版本配置，没有版本信息
        }
        
        int fileVersion = json.get("version").getAsInt();
        return fileVersion == version;
    }
    
    /**
     * 迁移配置（子类可以重写此方法实现版本迁移）
     * @param json JSON配置
     * @return 是否迁移成功
     */
    protected boolean migrateConfig(JsonObject json) {
        // 默认实现：重置为默认值
        resetToDefaults();
        return true;
    }
    
    /**
     * 设置配置值并触发变更监听
     * @param key 配置键
     * @param value 配置值
     */
    protected void setValue(String key, Object value) {
        Object oldValue = currentValues.get(key);
        currentValues.put(key, value);
        
        // 触发变更监听
        if (!equals(oldValue, value)) {
            // 触发本地监听器
            if (changeListener != null) {
                changeListener.onConfigChanged(this, key, oldValue, value);
            }
            
            // 触发全局监听器
            ConfigChangeEvent event = new ConfigChangeEvent(this, key, oldValue, value);
            ConfigManager.getInstance().getListenerManager().fireConfigChangeEvent(event);
        }
    }
    
    /**
     * 获取配置值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    protected <T> T getValue(String key, T defaultValue) {
        return (T) currentValues.getOrDefault(key, defaultValue);
    }
    
    /**
     * 比较两个对象是否相等
     */
    private boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
    
    /**
     * 从JSON对象获取值，如果不存在则使用默认值
     */
    protected <T> T getFromJson(JsonObject json, String key, T defaultValue, Class<T> type) {
        if (!json.has(key)) {
            return defaultValue;
        }
        
        JsonElement element = json.get(key);
        try {
            return GSON.fromJson(element, type);
        } catch (Exception e) {
            Deicore.LOGGER.warn("Failed to parse config value for key '{}', using default", key);
            return defaultValue;
        }
    }
}