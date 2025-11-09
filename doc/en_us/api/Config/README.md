# Deicore Configuration System API Documentation

## Overview

The Deicore configuration system provides a powerful, flexible configuration management framework specifically designed for Minecraft Fabric mods. It supports JSON format configuration saving and loading, with a complete change monitoring mechanism.

### Main Features

- **JSON Format Support**: Uses GSON library for JSON serialization and deserialization, making configuration files easy to read and edit
- **Version Management**: Supports configuration version control for backward compatibility and configuration migration
- **Change Monitoring**: Complete listener mechanism to monitor individual configuration items or global configuration changes
- **Thread Safety**: All configuration operations are thread-safe, suitable for multi-threaded environments
- **Annotation Driven**: Uses `@ConfigField` annotation to mark configuration fields with rich metadata support
- **Default Value Management**: Automatically manages default values to ensure configurations are always valid
- **Error Handling**: Comprehensive exception handling mechanism with detailed error information

## Quick Start

### 1. Create Configuration Class

Configuration classes need to extend the `AbstractConfig` base class and implement the necessary abstract methods. Here are detailed step-by-step instructions:

```
import org.dideng.com.deicore.client.api.config.*;

/**
 * Example configuration class demonstrating how to create custom configurations
 * Note: Configuration classes must extend AbstractConfig and implement abstract methods
 */
public class MyModConfig extends AbstractConfig {
    
    /**
     * Example only, not required
     * Use @ConfigField annotation to mark configuration fields
     * name: Configuration item key name in JSON file
     * description: Configuration item description
     * category: Configuration item category for UI grouping
     * visible: Whether to display in configuration interface (default: true)
     * editable: Whether editable (default: true)
     */
    @ConfigField(
        name = "enable_feature",
        description = "Enable my feature",
        category = "general"
    )
    private boolean enableFeature = true;
    
    /**
     * Example only, not required
     * Numeric configuration items can set minimum and maximum value constraints
     * min: Minimum value constraint
     * max: Maximum value constraint
     */
    @ConfigField(
        name = "max_count",
        description = "Maximum count",
        category = "settings",
        min = 1,
        max = 100
    )
    private int maxCount = 10;
    
    /**
     * Constructor must call parent constructor
     * @param configId Unique identifier for the configuration
     * @param configFileName Configuration file name
     * @param configDirectory Configuration file directory
     * @param version Configuration version number
     */
    public MyModConfig(String configId, String configFileName, Path configDirectory, int version) {
        super(configId, configFileName, configDirectory, version);
    }
    
    /**
     * Initialize default values
     * When configuration file doesn't exist or configuration items are missing, these default values will be used
     */
    @Override
    protected void initializeDefaults() {
        defaultValues.put("enableFeature", true);
        defaultValues.put("maxCount", 10);
    }
    
    /**
     * Serialize configuration object to JSON format
     * @return JSON object containing all configuration fields
     */
    @Override
    protected JsonObject serializeToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("enableFeature", enableFeature);
        json.addProperty("maxCount", maxCount);
        return json;
    }
    
    /**
     * Deserialize configuration values from JSON object
     * @param json JSON object containing configuration data
     */
    @Override
    protected void deserializeFromJson(JsonObject json) {
        // Use getFromJson method to safely get values from JSON
        // If the field doesn't exist in JSON, default values will be used
        enableFeature = getFromJson(json, "enableFeature", true, Boolean.class);
        maxCount = getFromJson(json, "maxCount", 10, Integer.class);
        
        // Set values to internal storage, this will trigger change listeners
        setValue("enableFeature", enableFeature);
        setValue("maxCount", maxCount);
    }
    
    // Getter and Setter methods
    // Note: Must use setter methods to modify configuration values to trigger change listeners
    
    public boolean isEnableFeature() {
        return enableFeature;
    }
    
    public void setEnableFeature(boolean enableFeature) {
        this.enableFeature = enableFeature;
        setValue("enableFeature", enableFeature);
    }
    
    public int getMaxCount() {
        return maxCount;
    }
    
    public void setMaxCount(int maxCount) {
        if (maxCount >= 1 && maxCount <= 100) {
            this.maxCount = maxCount;
            setValue("maxCount", maxCount);
        }
    }
}
```

**Important Notes:**
- All configuration fields must be marked with `@ConfigField` annotation
- Must implement `initializeDefaults()`, `serializeToJson()`, and `deserializeFromJson()` three abstract methods
- Must use setter methods to modify configuration values, not directly assign to fields
- Configuration classes should be thread-safe, avoid modifying field states at runtime

### 2. Register Configuration During Mod Initialization

Register the configuration in the mod's initialization method and set up change listeners. Here are detailed step-by-step instructions:

```
import org.dideng.com.deicore.client.api.config.DeicoreConfigAPI;

/**
 * Mod main class demonstrating how to integrate configuration system
 */
public class MyMod implements ModInitializer {
    private MyModConfig config;
    
    @Override
    public void onInitialize() {
        // Get singleton instance of configuration API
        // DeicoreConfigAPI uses singleton pattern to ensure only one configuration manager globally
        DeicoreConfigAPI configAPI = DeicoreConfigAPI.getInstance();
        
        // Register configuration
        // configId: Unique identifier for the configuration, used to distinguish different configurations
        // configClass: Configuration class Class object
        // fileName: Configuration file name (without path)
        // version: Configuration version number, used for version migration
        config = configAPI.registerConfig("mymod", MyModConfig.class, "mymod_config.json", 1);
        
        // Add configuration change listener
        // Listener will be called when configuration values change
        configAPI.addConfigSpecificListener("mymod", event -> {
            System.out.println("Configuration changed: " + event.getKey() + ": " + event.getOldValue() + " -> " + event.getNewValue());
            
            // Can execute corresponding logic based on configuration changes
            if ("enableFeature".equals(event.getKey())) {
                boolean newValue = (Boolean) event.getNewValue();
                if (newValue) {
                    // Enable feature
                    enableMyFeature();
                } else {
                    // Disable feature
                    disableMyFeature();
                }
            }
        });
        
        // Can also add global listener to monitor all configuration changes
        configAPI.addGlobalConfigChangeListener(event -> {
            System.out.println("Global configuration change - " + event.getConfig().getConfigId() + ": " + 
                             event.getKey() + " = " + event.getNewValue());
        });
    }
    
    private void enableMyFeature() {
        // Implementation for enabling feature
    }
    
    private void disableMyFeature() {
        // Implementation for disabling feature
    }
}
```

**Configuration Registration Notes:**
- `configId` must be unique and not duplicate with other configurations
- Configuration files will be automatically saved in Minecraft's configuration directory
- After registration, configuration will be automatically loaded, if file doesn't exist, default configuration will be created
- Listeners will trigger immediately when configuration values change

**Listener Type Description:**
- **Specific Configuration Listener**: Only monitors configuration changes for specified configId
- **Global Listener**: Monitors all configuration changes, suitable for logging or statistics

## API Reference

### DeicoreConfigAPI

The main entry point for the configuration system, providing singleton access. This is the most commonly used interface by developers.

#### Main Methods

- `getInstance()` - Get singleton instance of API, ensuring only one configuration manager globally
- `registerConfig(configId, configClass, fileName, version)` - Register new configuration
  - `configId`: Unique identifier for configuration (string)
  - `configClass`: Configuration class Class object (must extend AbstractConfig)
  - `fileName`: Configuration file name (e.g., "mymod_config.json")
  - `version`: Configuration version number (integer), used for version migration
- `saveAllConfigs()` - Save all registered configurations to files
- `reloadAllConfigs()` - Reload all configurations (read latest data from files)
- `addGlobalConfigChangeListener(listener)` - Add global configuration change listener
- `removeGlobalConfigChangeListener(listener)` - Remove global configuration change listener
- `addConfigSpecificListener(configId, listener)` - Add configuration-specific change listener
- `removeConfigSpecificListener(configId, listener)` - Remove configuration-specific change listener

#### Usage Example

```
// Get API instance
DeicoreConfigAPI api = DeicoreConfigAPI.getInstance();

// Register configuration
MyModConfig config = api.registerConfig("mymod", MyModConfig.class, "config.json", 1);

// Save all configurations
api.saveAllConfigs();

// Reload configurations
api.reloadAllConfigs();
```

### AbstractConfig

The base class for all configuration classes, providing common configuration functionality. Developers need to extend this class to create custom configurations.

#### Must Implement Abstract Methods

- `initializeDefaults()` - Initialize default values, used when configuration file doesn't exist
- `serializeToJson()` - Serialize configuration object to JSON format
- `deserializeFromJson(json)` - Deserialize configuration values from JSON object

#### Provided Protected Methods

- `getFromJson(json, key, defaultValue, type)` - Safely get value from JSON
- `setValue(key, value)` - Set configuration value and trigger listeners
- `getValue(key)` - Get configuration value
- `hasValueChanged(key, newValue)` - Check if value has changed

#### Lifecycle Methods

- `load()` - Load configuration (automatically called)
- `save()` - Save configuration to file
- `migrate(oldVersion)` - Configuration version migration (optional implementation)

### ConfigManager

Configuration manager, responsible for configuration registration and management. Usually not needed to use directly, accessed through DeicoreConfigAPI.

#### Main Responsibilities

- Manage all registered configurations
- Handle configuration file read/write operations
- Maintain configuration change listeners
- Ensure thread-safe configuration operations

### ConfigField Annotation

Annotation for marking configuration fields, providing rich metadata support.

#### Attribute Description

- `name` - Configuration item key name in JSON file (required)
- `description` - Configuration item description (optional)
- `category` - Configuration item category for UI grouping (optional)
- `visible` - Whether to display in configuration interface (default: true)
- `editable` - Whether editable (default: true)
- `min` - Minimum value constraint for numeric types (optional)
- `max` - Maximum value constraint for numeric types (optional)

#### Usage Example

```
@ConfigField(
    name = "feature_enabled",
    description = "Enable advanced feature",
    category = "advanced",
    visible = true,
    editable = true
)
private boolean featureEnabled = false;
```

### ConfigChangeEvent

Configuration change event class, containing detailed change information.

#### Attributes

- `config` - Configuration object where change occurred
- `key` - Changed configuration item key name
- `oldValue` - Value before change
- `newValue` - Value after change
- `timestamp` - Change timestamp

#### Usage Scenarios

Listeners receive ConfigChangeEvent objects and can execute corresponding logic based on event information.

## Configuration File Location

Configuration files are saved in Minecraft's configuration directory:
- Windows: `%APPDATA%\.minecraft\config\deicore\`
- Linux: `~/.minecraft/config/deicore/`
- macOS: `~/Library/Application Support/minecraft/config/deicore/`

## Example Configuration File

```json
{
  "version": 1,
  "configId": "mymod",
  "enableFeature": true,
  "maxCount": 10
}
```

## Advanced Usage

### Configuration Version Migration

When configuration structure changes, you can implement the `migrate()` method for version migration:

```
@Override
protected void migrate(int oldVersion) {
    if (oldVersion < 2) {
        // Migrate from version 1 to version 2
        // Rename fields or convert data formats
        if (hasValue("old_field_name")) {
            Object oldValue = getValue("old_field_name");
            setValue("new_field_name", oldValue);
            removeValue("old_field_name");
        }
    }
    if (oldVersion < 3) {
        // Migrate from version 2 to version 3
        // Add default values for new fields
        if (!hasValue("new_feature")) {
            setValue("new_feature", true);
        }
    }
}
```

### Configuration Validation

You can add custom validation logic in setter methods:

```
public void setMaxCount(int maxCount) {
    if (maxCount < 1) {
        throw new IllegalArgumentException("Maximum count cannot be less than 1");
    }
    if (maxCount > 100) {
        throw new IllegalArgumentException("Maximum count cannot be greater than 100");
    }
    this.maxCount = maxCount;
    setValue("maxCount", maxCount);
}
```

### Batch Configuration Operations

Use ConfigUtils utility class for batch operations:

```
// Batch save all configurations
ConfigUtils.saveAllConfigs();

// Batch reload configurations
ConfigUtils.reloadAllConfigs();

// Get specific configuration instance
MyModConfig config = ConfigUtils.getConfig("mymod", MyModConfig.class);

// Check if configuration is valid
if (ConfigUtils.isConfigValid("mymod")) {
    // Configuration is valid, can be safely used
}
```

## Troubleshooting

### Common Issues

1. **Configuration Loading Failure**
   - **Cause**: Insufficient configuration file path permissions or JSON format errors
   - **Solution**: 
     - Check read/write permissions for Minecraft configuration directory
     - Verify JSON file format is correct (can use JSON validation tools)
     - Check Minecraft log files for detailed error information

2. **Configuration Changes Not Triggering Listeners**
   - **Cause**: Not using setter methods or listener registration errors
   - **Solution**:
     - Ensure using configuration class setter methods to modify values, not directly assigning to fields
     - Check if listeners are correctly registered (configId matches)
     - Verify listener lambda expressions don't throw exceptions

3. **Configuration Values Not Saved to File**
   - **Cause**: Not calling save method or configuration not registered
   - **Solution**:
     - Ensure calling `config.save()` or `DeicoreConfigAPI.getInstance().saveAllConfigs()` after modifying configuration
     - Check if configuration is successfully registered to ConfigManager

4. **Configuration Version Migration Not Working**
   - **Cause**: migrate method not correctly implemented or version number not updated
   - **Solution**:
     - Ensure passing correct version number in configuration class constructor
     - Properly handle all old version migration paths in migrate method

### Debugging Tips

1. **Enable Detailed Logging**
   ```
   // Enable detailed logging during development
   System.setProperty("deicore.config.debug", "true");
   ```

2. **Check Configuration Status**
   ```
   // Check if configuration is loaded
   if (config.isLoaded()) {
       System.out.println("Configuration is loaded");
   }
   
   // Check if configuration has unsaved changes
   if (config.hasUnsavedChanges()) {
       System.out.println("Configuration has unsaved changes");
   }
   ```

3. **Listener Debugging**
   ```
   // Add debug listener
   configAPI.addGlobalConfigChangeListener(event -> {
       System.out.println("Debug - Configuration change: " + 
                         event.getConfig().getConfigId() + "." + event.getKey() + 
                         " = " + event.getNewValue());
   });
   ```

## Performance Optimization Suggestions

1. **Avoid Frequent Saves**: Don't call save() on every configuration change, can periodically batch save
2. **Use Appropriate Listeners**: Global listeners affect performance, try to use specific configuration listeners
3. **Lazy Loading**: For infrequently used configurations, consider lazy loading strategies
4. **Cache Configuration Values**: For frequently accessed configuration values, can cache locally

## Version History

### v1.0.0 (Current Version)
- **Basic Configuration Features**: Complete configuration management framework
- **JSON Format Support**: Using GSON for serialization/deserialization
- **Change Monitoring System**: Support for global and specific configuration listeners
- **Thread Safety**: All operations are thread-safe
- **Version Management**: Support for configuration version migration
- **Annotation Driven**: Using @ConfigField annotation to mark configuration fields
- **Default Value Management**: Automatic handling of default values and missing configurations
- **Error Handling**: Comprehensive exception handling mechanism

### Future Version Planning
- **Configuration UI Interface**: Automatically generate configuration interface
- **Hot Reload Support**: Automatically reload configuration files when modified
- **Network Synchronization**: Server-client configuration synchronization
- **Configuration Templates**: Predefined configuration template system

## Contribution Guidelines

Welcome to contribute code to the Deicore configuration system! Please follow these guidelines:

1. **Code Style**: Follow Java coding standards, use 4-space indentation
2. **Test Coverage**: New features need to include unit tests
3. **Documentation Updates**: When modifying code, need to synchronously update documentation
4. **Backward Compatibility**: Ensure modifications don't break existing functionality

## Technical Support

If you encounter problems during use, you can get help through the following ways:

1. **Check Logs**: Minecraft log files contain detailed error information
2. **Community Support**: Visit project GitHub page to submit issues
3. **Documentation Reference**: Detailed usage instructions and API documentation
4. **Example Code**: Reference provided example configuration classes