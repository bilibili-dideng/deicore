package org.dideng.com.deicore.client.api.config;

/**
 * 配置变更事件
 */
public class ConfigChangeEvent {
    
    private final IConfig config;
    private final String key;
    private final Object oldValue;
    private final Object newValue;
    private final long timestamp;
    
    public ConfigChangeEvent(IConfig config, String key, Object oldValue, Object newValue) {
        this.config = config;
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = System.currentTimeMillis();
    }
    
    public IConfig getConfig() {
        return config;
    }
    
    public String getKey() {
        return key;
    }
    
    public Object getOldValue() {
        return oldValue;
    }
    
    public Object getNewValue() {
        return newValue;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("ConfigChangeEvent{config=%s, key='%s', oldValue=%s, newValue=%s, timestamp=%d}",
                config.getConfigId(), key, oldValue, newValue, timestamp);
    }
}