# Deicore 配置系统 API 参考

## 核心接口和类

### IConfig 接口

配置系统的核心接口，定义了配置的基本操作。

**方法：**
- `String getConfigId()` - 获取配置ID
- `String getConfigFileName()` - 获取配置文件名
- `Path getConfigDirectory()` - 获取配置目录
- `int getVersion()` - 获取配置版本
- `boolean load()` - 加载配置
- `boolean save()` - 保存配置
- `boolean migrate(int oldVersion)` - 配置迁移
- `void addConfigChangeListener(ConfigChangeListener listener)` - 添加配置变更监听器
- `void removeConfigChangeListener(ConfigChangeListener listener)` - 移除配置变更监听器

**内部接口：**
- `ConfigChangeListener` - 配置变更监听器接口

### AbstractConfig 抽象类

配置类的基类，实现了IConfig接口，提供通用的配置功能。

**构造函数：**
```java
public AbstractConfig(String configId, String configFileName, Path configDirectory, int version) {}
```

**保护方法（需要子类实现）：**
- `protected abstract void initializeDefaults()` - 初始化默认值
- `protected abstract JsonObject serializeToJson()` - 序列化为JSON
- `protected abstract void deserializeFromJson(JsonObject json)` - 从JSON反序列化

**公共方法：**
- `boolean setValue(String key, Object value)` - 设置配置值
- `Object getValue(String key)` - 获取配置值
- `Object getDefaultValue(String key)` - 获取默认值
- `boolean hasValue(String key)` - 检查是否有值
- `void resetToDefault(String key)` - 重置为默认值
- `void resetAllToDefaults()` - 重置所有值为默认值

**工具方法：**
- `protected <T> T getFromJson(JsonObject json, String key, T defaultValue, Class<T> type)` - 从JSON安全获取值

### ConfigField 注解

用于标记配置字段的注解。

**属性：**
- `String name()` - 字段名称（默认：字段名）
- `String description()` - 字段描述
- `String category()` - 字段分类
- `boolean visible()` - 是否可见（默认：true）
- `double min()` - 最小值（数字类型）
- `double max()` - 最大值（数字类型）
- `boolean editable()` - 是否可编辑（默认：true）

### ConfigManager 类

配置管理器，负责配置的注册和管理。

**单例模式：**
- `private static final ConfigManager INSTANCE = new ConfigManager()`
- `public static ConfigManager getInstance()`

**核心方法：**
- `public <T extends AbstractConfig> T registerConfig(String configId, Class<T> configClass, String fileName, int version)` - 注册配置
- `public <T extends AbstractConfig> T getConfig(String configId)` - 获取配置实例
- `public boolean saveAllConfigs()` - 保存所有配置
- `public boolean reloadAllConfigs()` - 重新加载所有配置
- `public Set<String> getRegisteredConfigIds()` - 获取已注册的配置ID

**监听器管理：**
- `public void addGlobalConfigChangeListener(Consumer<ConfigChangeEvent> listener)` - 添加全局监听器
- `public void removeGlobalConfigChangeListener(Consumer<ConfigChangeEvent> listener)` - 移除全局监听器
- `public void addConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - 添加特定配置监听器
- `public void removeConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - 移除特定配置监听器
- `public ConfigChangeListenerManager getListenerManager()` - 获取监听器管理器

### ConfigChangeEvent 类

配置变更事件，封装配置变更信息。

**属性：**
- `private final AbstractConfig config` - 配置对象
- `private final String key` - 变更的键名
- `private final Object oldValue` - 旧值
- `private final Object newValue` - 新值
- `private final long timestamp` - 时间戳

**Getter方法：**
- `public AbstractConfig getConfig()`
- `public String getKey()`
- `public Object getOldValue()`
- `public Object getNewValue()`
- `public long getTimestamp()`

### ConfigChangeListenerManager 类

配置变更监听器管理器，负责监听器的管理。

**方法：**
- `public void addGlobalListener(Consumer<ConfigChangeEvent> listener)` - 添加全局监听器
- `public void removeGlobalListener(Consumer<ConfigChangeEvent> listener)` - 移除全局监听器
- `public void addConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - 添加特定配置监听器
- `public void removeConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - 移除特定配置监听器
- `public void fireConfigChangeEvent(ConfigChangeEvent event)` - 触发配置变更事件
- `public int getGlobalListenerCount()` - 获取全局监听器数量
- `public int getConfigSpecificListenerCount(String configId)` - 获取特定配置监听器数量
- `public void clearAllListeners()` - 清除所有监听器

### DeicoreConfigAPI 类

配置系统的API入口点，提供便捷的访问方式。

**单例模式：**
- `private static final DeicoreConfigAPI INSTANCE = new DeicoreConfigAPI()`
- `public static DeicoreConfigAPI getInstance()`

**核心方法：**
- `public ConfigManager getConfigManager()` - 获取配置管理器
- `public <T extends AbstractConfig> T registerConfig(String configId, Class<T> configClass, String fileName, int version)` - 注册配置
- `public <T extends AbstractConfig> T getConfig(String configId)` - 获取配置
- `public boolean saveAllConfigs()` - 保存所有配置
- `public boolean reloadAllConfigs()` - 重新加载所有配置

**监听器管理：**
- `public void addGlobalConfigChangeListener(Consumer<ConfigChangeEvent> listener)` - 添加全局监听器
- `public void removeGlobalConfigChangeListener(Consumer<ConfigChangeEvent> listener)` - 移除全局监听器
- `public void addConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - 添加特定配置监听器
- `public void removeConfigSpecificListener(String configId, Consumer<ConfigChangeEvent> listener)` - 移除特定配置监听器

### ConfigUtils 类

配置工具类，提供便捷的操作方法。

**静态方法：**
- `public static <T extends AbstractConfig> T registerAndGetConfig(String configId, Class<T> configClass, String fileName, int version)` - 注册并获取配置
- `public static <T extends AbstractConfig> T getConfig(String configId)` - 获取配置
- `public static AbstractConfig createDefaultConfig(String configId, String fileName, int version)` - 创建默认配置
- `public static boolean isConfigValid(AbstractConfig config)` - 检查配置有效性
- `public static Object getConfigValueSafely(AbstractConfig config, String key, Object defaultValue)` - 安全获取配置值
- `public static boolean saveAllConfigs()` - 保存所有配置
- `public static boolean reloadAllConfigs()` - 重新加载所有配置
- `public static void addGlobalConfigChangeListener(Consumer<ConfigChangeEvent> listener)` - 添加全局监听器

## 使用示例

### 基本配置类

```java
public class MyModConfig extends AbstractConfig {
    @ConfigField(name = "enable_feature", description = "启用功能", category = "general")
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

### 模组初始化

```java
public class MyMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // 使用API方式
        DeicoreConfigAPI api = DeicoreConfigAPI.getInstance();
        MyModConfig config = api.registerConfig("mymod", MyModConfig.class, "mymod.json", 1);
        
        // 使用工具类方式
        MyModConfig config2 = ConfigUtils.registerAndGetConfig("mymod2", MyModConfig.class, "mymod2.json", 1);
        
        // 添加监听器
        api.addConfigSpecificListener("mymod", event -> {
            System.out.println("配置变更: " + event.getKey());
        });
    }
}
```