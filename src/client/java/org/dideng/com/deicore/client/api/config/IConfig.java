package org.dideng.com.deicore.client.api.config;

import java.nio.file.Path;

/**
 * 配置接口，定义配置的基本操作
 */
public interface IConfig {
    
    /**
     * 获取配置的唯一标识符
     * @return 配置ID
     */
    String getConfigId();
    
    /**
     * 获取配置文件名
     * @return 配置文件名
     */
    String getConfigFileName();
    
    /**
     * 获取配置文件的完整路径
     * @return 配置文件路径
     */
    Path getConfigFilePath();
    
    /**
     * 加载配置
     * @return 是否加载成功
     */
    boolean load();
    
    /**
     * 保存配置
     * @return 是否保存成功
     */
    boolean save();
    
    /**
     * 重置为默认值
     */
    void resetToDefaults();
    
    /**
     * 检查配置是否已加载
     * @return 是否已加载
     */
    boolean isLoaded();
    
    /**
     * 获取配置版本
     * @return 配置版本
     */
    int getVersion();
    
    /**
     * 设置配置变更监听器
     * @param listener 监听器
     */
    void setChangeListener(ConfigChangeListener listener);
    
    /**
     * 配置变更监听器接口
     */
    interface ConfigChangeListener {
        /**
         * 当配置发生变更时调用
         * @param config 发生变更的配置
         * @param key 变更的配置项键
         * @param oldValue 旧值
         * @param newValue 新值
         */
        void onConfigChanged(IConfig config, String key, Object oldValue, Object newValue);
    }
}