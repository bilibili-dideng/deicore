package org.dideng.com.deicore.client.api.config;

import org.dideng.com.deicore.Deicore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 配置变更监听器管理器
 */
public class ConfigChangeListenerManager {
    
    private final List<IConfig.ConfigChangeListener> globalListeners = new CopyOnWriteArrayList<>();
    private final List<ConfigSpecificListener> configSpecificListeners = new CopyOnWriteArrayList<>();
    
    /**
     * 添加全局配置变更监听器
     */
    public void addGlobalListener(IConfig.ConfigChangeListener listener) {
        globalListeners.add(listener);
        Deicore.LOGGER.debug("Added global config change listener");
    }
    
    /**
     * 移除全局配置变更监听器
     */
    public void removeGlobalListener(IConfig.ConfigChangeListener listener) {
        globalListeners.remove(listener);
        Deicore.LOGGER.debug("Removed global config change listener");
    }
    
    /**
     * 添加特定配置的变更监听器
     */
    public void addConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener) {
        ConfigSpecificListener wrapper = new ConfigSpecificListener(configId, listener);
        configSpecificListeners.add(wrapper);
        Deicore.LOGGER.debug("Added config-specific listener for: {}", configId);
    }
    
    /**
     * 移除特定配置的变更监听器
     */
    public void removeConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener) {
        configSpecificListeners.removeIf(wrapper -> 
            wrapper.configId.equals(configId) && wrapper.listener == listener);
        Deicore.LOGGER.debug("Removed config-specific listener for: {}", configId);
    }
    
    /**
     * 触发配置变更事件
     */
    public void fireConfigChangeEvent(ConfigChangeEvent event) {
        // 触发全局监听器
        for (IConfig.ConfigChangeListener listener : globalListeners) {
            try {
                listener.onConfigChanged(event.getConfig(), event.getKey(), event.getOldValue(), event.getNewValue());
            } catch (Exception e) {
                Deicore.LOGGER.error("Error in global config change listener", e);
            }
        }
        
        // 触发特定配置监听器
        String configId = event.getConfig().getConfigId();
        for (ConfigSpecificListener wrapper : configSpecificListeners) {
            if (wrapper.configId.equals(configId)) {
                try {
                    wrapper.listener.accept(event);
                } catch (Exception e) {
                    Deicore.LOGGER.error("Error in config-specific listener for: {}", configId, e);
                }
            }
        }
        
        Deicore.LOGGER.debug("Fired config change event: {}", event);
    }
    
    /**
     * 获取全局监听器数量
     */
    public int getGlobalListenerCount() {
        return globalListeners.size();
    }
    
    /**
     * 获取特定配置的监听器数量
     */
    public int getConfigSpecificListenerCount(String configId) {
        return (int) configSpecificListeners.stream()
                .filter(wrapper -> wrapper.configId.equals(configId))
                .count();
    }
    
    /**
     * 清除所有监听器
     */
    public void clearAllListeners() {
        globalListeners.clear();
        configSpecificListeners.clear();
        Deicore.LOGGER.info("Cleared all config change listeners");
    }
    
    /**
     * 特定配置监听器包装类
     */
    private static class ConfigSpecificListener {
        final String configId;
        final Consumer<ConfigChangeEvent> listener;
        
        ConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener) {
            this.configId = configId;
            this.listener = listener;
        }
    }
}