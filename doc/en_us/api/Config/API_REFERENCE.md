# Deicore Configuration System API Reference

## Core Interfaces and Classes

### IConfig Interface

The core interface of the configuration system, defining basic configuration operations.

**Methods:**
- `String getConfigId()` - Get configuration ID
- `String getConfigFileName()` - Get configuration file name
- `Path getConfigDirectory()` - Get configuration directory
- `int getVersion()` - Get configuration version
- `boolean load()` - Load configuration
- `boolean save()` - Save configuration
- `boolean migrate(int oldVersion)` - Configuration migration
- `void addConfigChangeListener(ConfigChangeListener listener)` - Add configuration change listener
- `void removeConfigChangeListener(ConfigChangeListener listener)` - Remove configuration change listener

**Internal Interfaces:**
- `ConfigChangeListener` - Configuration change listener interface

### AbstractConfig Abstract Class

The base class for configuration classes, implementing the IConfig interface and providing common configuration functionality.

**Constructor:**
```java
public AbstractConfig(String configId, String configFileName, Path configDirectory, int version) {}
```

**Protected Methods (to be implemented by subclasses):**
- `protected abstract void initializeDefaults()` - Initialize default values
- `protected abstract JsonObject serializeToJson()` - Serialize to JSON
- `protected abstract void deserializeFromJson(JsonObject json)` - Deserialize from JSON

**Public Methods:**
- `boolean setValue(String key, Object value)` - Set configuration value
- `Object getValue(String key)` - Get configuration value
- `Object getDefaultValue(String key)` - Get default value
- `boolean hasValue(String key)` - Check if value exists
- `void resetToDefault(String key)` - Reset to default value
- `void resetAllToDefaults()` - Reset all values to defaults

**Utility Methods:**
- `protected <T> T getFromJson(JsonObject json, String key, T defaultValue, Class<T> type)` - Safely get value from JSON

### ConfigField Annotation

Annotation for marking configuration fields.

**Attributes:**
- `String name()` - Field name (default: field name)
- `String description()` - Field description
- `String category()` - Field category
- `boolean visible()` - Whether visible (default: true)
- `double min()` - Minimum value (numeric types)
- `double max()` - Maximum value (numeric types)
- `boolean editable()` - Whether editable (default: true)

### ConfigManager Class

Configuration manager, responsible for configuration registration and management.

**Singleton Pattern:**
- `private static final ConfigManager INSTANCE = new ConfigManager()`
- `public static ConfigManager getInstance()`

**Core Methods:**
- `public <T extends AbstractConfig> T registerConfig(String configId, Class<T> configClass, String fileName, int version)` - Register configuration
- `public <T extends AbstractConfig> T getConfig(String configId)` - Get configuration instance
- `public boolean saveAllConfigs()` - Save all configurations
- `public boolean reloadAllConfigs()` - Reload all configurations
- `public Set<String> getRegisteredConfigIds()` - Get registered configuration IDs

**Listener Management:**
- `public void addGlobalConfigChangeListener(Consumer<ConfigChangeEvent> listener)` - Add global listener
- `public void removeGlobalConfigChangeListener(Consumer<ConfigChangeEvent> listener)` - Remove global listener
- `public void addConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - Add configuration-specific listener
- `public void removeConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - Remove configuration-specific listener
- `public ConfigChangeListenerManager getListenerManager()` - Get listener manager

### ConfigChangeEvent Class

Configuration change event, encapsulating configuration change information.

**Attributes:**
- `private final AbstractConfig config` - Configuration object
- `private final String key` - Changed key name
- `private final Object oldValue` - Old value
- `private final Object newValue` - New value
- `private final long timestamp` - Timestamp

**Getter Methods:**
- `public AbstractConfig getConfig()`
- `public String getKey()`
- `public Object getOldValue()`
- `public Object getNewValue()`
- `public long getTimestamp()`

### ConfigChangeListenerManager Class

Configuration change listener manager, responsible for listener management.

**Methods:**
- `public void addGlobalListener(Consumer<ConfigChangeEvent> listener)` - Add global listener
- `public void removeGlobalListener(Consumer<ConfigChangeEvent> listener)` - Remove global listener
- `public void addConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - Add configuration-specific listener
- `public void removeConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - Remove configuration-specific listener
- `public void fireConfigChangeEvent(ConfigChangeEvent event)` - Trigger configuration change event
- `public int getGlobalListenerCount()` - Get global listener count
- `public int getConfigSpecificListenerCount(String configId)` - Get configuration-specific listener count
- `public void clearAllListeners()` - Clear all listeners

### DeicoreConfigAPI Class

Configuration system API entry point, providing convenient access methods.

**Singleton Pattern:**
- `private static final DeicoreConfigAPI INSTANCE = new DeicoreConfigAPI()`
- `public static DeicoreConfigAPI getInstance()`

**Core Methods:**
- `public ConfigManager getConfigManager()` - Get configuration manager
- `public <T extends AbstractConfig> T registerConfig(String configId, Class<T> configClass, String fileName, int version)` - Register configuration
- `public <T extends AbstractConfig> T getConfig(String configId)` - Get configuration
- `public boolean saveAllConfigs()` - Save all configurations
- `public boolean reloadAllConfigs()` - Reload all configurations

**Listener Management:**
- `public void addGlobalConfigChangeListener(Consumer<ConfigChangeEvent> listener)` - Add global listener
- `public void removeGlobalConfigChangeListener(Consumer<ConfigChangeEvent> listener)` - Remove global listener
- `public void addConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - Add configuration-specific listener
- `public void removeConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - Remove configuration-specific listener

### ConfigUtils Class

Configuration utility class, providing convenient operation methods.

**Static Methods:**
- `public static <T extends AbstractConfig> T registerAndGetConfig(String configId, Class<T> configClass, String fileName, int version)` - Register and get configuration
- `public static <T extends AbstractConfig> T getConfig(String configId)` - Get configuration
- `public static AbstractConfig createDefaultConfig(String configId, String fileName, int version)` - Create default configuration
- `public static boolean isConfigValid(AbstractConfig config)` - Check configuration validity
- `public static Object getConfigValueSafely(AbstractConfig config, String key, Object defaultValue)` - Safely get configuration value
- `public static boolean saveAllConfigs()` - Save all configurations
- `public static boolean reloadAllConfigs()` - Reload all configurations
- `public static void addGlobalConfigChangeListener(Consumer<ConfigChangeEvent> listener)` - Add global listener

## Usage Examples

### Basic Configuration Class

```java
public class MyModConfig extends AbstractConfig {
    @ConfigField(name = "enable_feature", description = "Enable feature", category = "general")
    private boolean enableFeature = true;
    
    public MyModConfig(String configId, String configFileName, Path configDirectory, int version) {
        super(configId, configFileName, configDirectory, version);
    }
    
    @Override
    protected void initializeDefaults() {
        defaultValues.put("enableFeature", true);
    }
    
    @Override
    protected JsonObject serializeToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("enableFeature", enableFeature);
        return json;
    }
    
    @Override
    protected void deserializeFromJson(JsonObject json) {
        enableFeature = getFromJson(json, "enableFeature", true, Boolean.class);
        setValue("enableFeature", enableFeature);
    }
    
    public boolean isEnableFeature() { return enableFeature; }
    public void setEnableFeature(boolean value) { 
        this.enableFeature = value; 
        setValue("enableFeature", value);
    }
}
```

### Mod Initialization

```java
public class MyMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Using API approach
        DeicoreConfigAPI api = DeicoreConfigAPI.getInstance();
        MyModConfig config = api.registerConfig("mymod", MyModConfig.class, "mymod.json", 1);
        
        // Using utility class approach
        MyModConfig config2 = ConfigUtils.registerAndGetConfig("mymod2", MyModConfig.class, "mymod2.json", 1);
        
        // Add listener
        api.addConfigSpecificListener("mymod", event -> {
            System.out.println("Configuration changed: " + event.getKey());
        });
    }
}
```