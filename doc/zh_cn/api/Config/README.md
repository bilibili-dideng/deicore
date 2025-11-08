# Deicore 配置系统 API 文档

## 概述

Deicore配置系统提供了一个强大、灵活的配置管理框架，专门为Minecraft Fabric模组设计。支持JSON格式的配置保存和加载，包含完整的变更监听机制。

### 主要特性

- **JSON格式支持**：使用GSON库进行JSON序列化和反序列化，配置文件易于阅读和编辑
- **版本管理**：支持配置版本控制，便于向后兼容和配置迁移
- **变更监听**：完整的监听器机制，可以监听单个配置项或全局配置变更
- **线程安全**：所有配置操作都是线程安全的，适合多线程环境
- **注解驱动**：使用`@ConfigField`注解标记配置字段，支持丰富的元数据
- **默认值管理**：自动管理默认值，确保配置始终有效
- **错误处理**：完善的异常处理机制，提供详细的错误信息

## 快速开始

### 1. 创建配置类

配置类需要继承`AbstractConfig`基类，并实现必要的抽象方法。以下是详细的步骤说明：

```
import org.dideng.com.deicore.client.api.config.*;

/**
 * 示例配置类，演示如何创建自定义配置
 * 注意：配置类必须继承AbstractConfig并实现抽象方法
 */
public class MyModConfig extends AbstractConfig {
    
    /**
     * 仅示例，非必须
     * 使用@ConfigField注解标记配置字段
     * name: 配置项在JSON文件中的键名
     * description: 配置项的描述信息
     * category: 配置项的分类，用于UI分组
     * visible: 是否在配置界面显示（默认为true）
     * editable: 是否可编辑（默认为true）
     */
    @ConfigField(
        name = "enable_feature",
        description = "启用我的功能",
        category = "general"
    )
    private boolean enableFeature = true;
    
    /**
     * 仅示例，非必须
     * 数值类型的配置项可以设置最小值和最大值限制
     * min: 最小值限制
     * max: 最大值限制
     */
    @ConfigField(
        name = "max_count",
        description = "最大数量",
        category = "settings",
        min = 1,
        max = 100
    )
    private int maxCount = 10;
    
    /**
     * 构造函数必须调用父类构造函数
     * @param configId 配置的唯一标识符
     * @param configFileName 配置文件名
     * @param configDirectory 配置文件目录
     * @param version 配置版本号
     */
    public MyModConfig(String configId, String configFileName, Path configDirectory, int version) {
        super(configId, configFileName, configDirectory, version);
    }
    
    /**
     * 初始化默认值
     * 当配置文件不存在或配置项缺失时，会使用这些默认值
     */
    @Override
    protected void initializeDefaults() {
        defaultValues.put("enableFeature", true);
        defaultValues.put("maxCount", 10);
    }
    
    /**
     * 将配置对象序列化为JSON格式
     * @return 包含所有配置字段的JSON对象
     */
    @Override
    protected JsonObject serializeToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("enableFeature", enableFeature);
        json.addProperty("maxCount", maxCount);
        return json;
    }
    
    /**
     * 从JSON对象反序列化配置值
     * @param json 包含配置数据的JSON对象
     */
    @Override
    protected void deserializeFromJson(JsonObject json) {
        // 使用getFromJson方法安全地从JSON获取值
        // 如果JSON中不存在该字段，会使用默认值
        enableFeature = getFromJson(json, "enableFeature", true, Boolean.class);
        maxCount = getFromJson(json, "maxCount", 10, Integer.class);
        
        // 设置值到内部存储，这会触发变更监听器
        setValue("enableFeature", enableFeature);
        setValue("maxCount", maxCount);
    }
    
    // Getter和Setter方法
    // 注意：必须使用setter方法修改配置值，这样才能触发变更监听器
    
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

**重要说明：**
- 所有配置字段必须使用`@ConfigField`注解标记
- 必须实现`initializeDefaults()`、`serializeToJson()`和`deserializeFromJson()`三个抽象方法
- 修改配置值时必须使用setter方法，不能直接赋值给字段
- 配置类应该是线程安全的，避免在运行时修改字段状态

### 2. 在模组初始化时注册配置

在模组的初始化方法中注册配置，并设置变更监听器。以下是详细的步骤说明：

```
import org.dideng.com.deicore.client.api.config.DeicoreConfigAPI;

/**
 * 模组主类，演示如何集成配置系统
 */
public class MyMod implements ModInitializer {
    private MyModConfig config;
    
    @Override
    public void onInitialize() {
        // 获取配置API的单例实例
        // DeicoreConfigAPI采用单例模式，确保全局只有一个配置管理器
        DeicoreConfigAPI configAPI = DeicoreConfigAPI.getInstance();
        
        // 注册配置
        // configId: 配置的唯一标识符，用于区分不同的配置
        // configClass: 配置类的Class对象
        // fileName: 配置文件名（不含路径）
        // version: 配置版本号，用于版本迁移
        config = configAPI.registerConfig("mymod", MyModConfig.class, "mymod_config.json", 1);
        
        // 添加配置变更监听器
        // 监听器会在配置值发生变化时被调用
        configAPI.addConfigSpecificListener("mymod", event -> {
            System.out.println("配置变更: " + event.getKey() + ": " + event.getOldValue() + " -> " + event.getNewValue());
            
            // 可以根据配置变更执行相应的逻辑
            if ("enableFeature".equals(event.getKey())) {
                boolean newValue = (Boolean) event.getNewValue();
                if (newValue) {
                    // 启用功能
                    enableMyFeature();
                } else {
                    // 禁用功能
                    disableMyFeature();
                }
            }
        });
        
        // 也可以添加全局监听器，监听所有配置的变更
        configAPI.addGlobalConfigChangeListener(event -> {
            System.out.println("全局配置变更 - " + event.getConfig().getConfigId() + ": " + 
                             event.getKey() + " = " + event.getNewValue());
        });
    }
    
    private void enableMyFeature() {
        // 启用功能的实现
    }
    
    private void disableMyFeature() {
        // 禁用功能的实现
    }
}
```

**注册配置的注意事项：**
- `configId`必须是唯一的，不能与其他配置重复
- 配置文件会自动保存在Minecraft的配置目录下
- 注册后配置会自动加载，如果文件不存在会创建默认配置
- 监听器会在配置值发生变化时立即触发

**监听器类型说明：**
- **特定配置监听器**：只监听指定configId的配置变更
- **全局监听器**：监听所有配置的变更，适合日志记录或统计

## API 参考

### DeicoreConfigAPI

配置系统的主入口点，提供单例访问。这是开发者最常用的接口。

#### 主要方法

- `getInstance()` - 获取API的单例实例，确保全局只有一个配置管理器
- `registerConfig(configId, configClass, fileName, version)` - 注册新的配置
  - `configId`: 配置的唯一标识符（字符串）
  - `configClass`: 配置类的Class对象（必须继承AbstractConfig）
  - `fileName`: 配置文件名（如"mymod_config.json"）
  - `version`: 配置版本号（整数），用于版本迁移
- `saveAllConfigs()` - 保存所有已注册的配置到文件
- `reloadAllConfigs()` - 重新加载所有配置（从文件读取最新数据）
- `addGlobalConfigChangeListener(listener)` - 添加全局配置变更监听器
- `removeGlobalConfigChangeListener(listener)` - 移除全局配置变更监听器
- `addConfigSpecificListener(configId, listener)` - 添加特定配置的变更监听器
- `removeConfigSpecificListener(configId, listener)` - 移除特定配置的变更监听器

#### 使用示例

```
// 获取API实例
DeicoreConfigAPI api = DeicoreConfigAPI.getInstance();

// 注册配置
MyModConfig config = api.registerConfig("mymod", MyModConfig.class, "config.json", 1);

// 保存所有配置
api.saveAllConfigs();

// 重新加载配置
api.reloadAllConfigs();
```

### AbstractConfig

所有配置类的基类，提供通用的配置功能。开发者需要继承这个类来创建自定义配置。

#### 必须实现的抽象方法

- `initializeDefaults()` - 初始化默认值，当配置文件不存在时使用
- `serializeToJson()` - 将配置对象序列化为JSON格式
- `deserializeFromJson(json)` - 从JSON对象反序列化配置值

#### 提供的保护方法

- `getFromJson(json, key, defaultValue, type)` - 安全地从JSON获取值
- `setValue(key, value)` - 设置配置值并触发监听器
- `getValue(key)` - 获取配置值
- `hasValueChanged(key, newValue)` - 检查值是否发生变化

#### 生命周期方法

- `load()` - 加载配置（自动调用）
- `save()` - 保存配置到文件
- `migrate(oldVersion)` - 配置版本迁移（可选实现）

### ConfigManager

配置管理器，负责配置的注册和管理。通常不需要直接使用，通过DeicoreConfigAPI访问。

#### 主要职责

- 管理所有已注册的配置
- 处理配置文件的读写操作
- 维护配置变更监听器
- 确保线程安全的配置操作

### ConfigField 注解

用于标记配置字段的注解，提供丰富的元数据支持。

#### 属性说明

- `name` - 配置项在JSON文件中的键名（必需）
- `description` - 配置项的描述信息（可选）
- `category` - 配置项的分类，用于UI分组（可选）
- `visible` - 是否在配置界面显示（默认true）
- `editable` - 是否可编辑（默认true）
- `min` - 数值类型的最小值限制（可选）
- `max` - 数值类型的最大值限制（可选）

#### 使用示例

```
@ConfigField(
    name = "feature_enabled",
    description = "启用高级功能",
    category = "advanced",
    visible = true,
    editable = true
)
private boolean featureEnabled = false;
```

### ConfigChangeEvent

配置变更事件类，包含变更的详细信息。

#### 属性

- `config` - 发生变更的配置对象
- `key` - 变更的配置项键名
- `oldValue` - 变更前的值
- `newValue` - 变更后的值
- `timestamp` - 变更时间戳

#### 使用场景

监听器接收ConfigChangeEvent对象，可以根据事件信息执行相应逻辑。

## 配置文件位置

配置文件保存在Minecraft的配置目录下：
- Windows: `%APPDATA%\.minecraft\config\deicore\`
- Linux: `~/.minecraft/config/deicore/`
- macOS: `~/Library/Application Support/minecraft/config/deicore/`

## 示例配置文件

```json
{
  "version": 1,
  "configId": "mymod",
  "enableFeature": true,
  "maxCount": 10
}
```

## 高级用法

### 配置版本迁移

当配置结构发生变化时，可以实现`migrate()`方法进行版本迁移：

```
@Override
protected void migrate(int oldVersion) {
    if (oldVersion < 2) {
        // 从版本1迁移到版本2
        // 重命名字段或转换数据格式
        if (hasValue("old_field_name")) {
            Object oldValue = getValue("old_field_name");
            setValue("new_field_name", oldValue);
            removeValue("old_field_name");
        }
    }
    if (oldVersion < 3) {
        // 从版本2迁移到版本3
        // 添加新字段的默认值
        if (!hasValue("new_feature")) {
            setValue("new_feature", true);
        }
    }
}
```

### 配置验证

可以在setter方法中添加自定义验证逻辑：

```
public void setMaxCount(int maxCount) {
    if (maxCount < 1) {
        throw new IllegalArgumentException("最大数量不能小于1");
    }
    if (maxCount > 100) {
        throw new IllegalArgumentException("最大数量不能大于100");
    }
    this.maxCount = maxCount;
    setValue("maxCount", maxCount);
}
```

### 批量操作配置

使用ConfigUtils工具类进行批量操作：

```
// 批量保存所有配置
ConfigUtils.saveAllConfigs();

// 批量重新加载配置
ConfigUtils.reloadAllConfigs();

// 获取特定配置实例
MyModConfig config = ConfigUtils.getConfig("mymod", MyModConfig.class);

// 检查配置是否有效
if (ConfigUtils.isConfigValid("mymod")) {
    // 配置有效，可以安全使用
}
```

## 故障排除

### 常见问题

1. **配置加载失败**
   - **原因**: 配置文件路径权限不足或JSON格式错误
   - **解决方案**: 
     - 检查Minecraft配置目录的读写权限
     - 验证JSON文件格式是否正确（可以使用JSON验证工具）
     - 查看Minecraft日志文件获取详细的错误信息

2. **配置变更未触发监听器**
   - **原因**: 未使用setter方法或监听器注册错误
   - **解决方案**:
     - 确保使用配置类的setter方法修改值，而不是直接赋值给字段
     - 检查监听器是否正确注册（configId是否匹配）
     - 验证监听器lambda表达式没有抛出异常

3. **配置值未保存到文件**
   - **原因**: 未调用save方法或配置未注册
   - **解决方案**:
     - 确保在修改配置后调用`config.save()`或`DeicoreConfigAPI.getInstance().saveAllConfigs()`
     - 检查配置是否成功注册到ConfigManager

4. **配置版本迁移不工作**
   - **原因**: migrate方法未正确实现或版本号未更新
   - **解决方案**:
     - 确保在配置类构造函数中传递正确的版本号
     - 在migrate方法中正确处理所有旧版本迁移路径

### 调试技巧

1. **启用详细日志**
   ```
   // 在开发阶段启用详细日志
   System.setProperty("deicore.config.debug", "true");
   ```

2. **检查配置状态**
   ```
   // 检查配置是否已加载
   if (config.isLoaded()) {
       System.out.println("配置已加载");
   }
   
   // 检查配置是否有未保存的变更
   if (config.hasUnsavedChanges()) {
       System.out.println("配置有未保存的变更");
   }
   ```

3. **监听器调试**
   ```
   // 添加调试监听器
   configAPI.addGlobalConfigChangeListener(event -> {
       System.out.println("调试 - 配置变更: " + 
                         event.getConfig().getConfigId() + "." + event.getKey() + 
                         " = " + event.getNewValue());
   });
   ```

## 性能优化建议

1. **避免频繁保存**：不要在每次配置变更时都调用save()，可以定期批量保存
2. **使用合适的监听器**：全局监听器会影响性能，尽量使用特定配置监听器
3. **延迟加载**：对于不常用的配置，可以考虑延迟加载策略
4. **缓存配置值**：对于频繁访问的配置值，可以在本地缓存

## 版本历史

### v1.0.0 (当前版本)
- **基础配置功能**：完整的配置管理框架
- **JSON格式支持**：使用GSON进行序列化/反序列化
- **变更监听系统**：支持全局和特定配置监听器
- **线程安全**：所有操作都是线程安全的
- **版本管理**：支持配置版本迁移
- **注解驱动**：使用@ConfigField注解标记配置字段
- **默认值管理**：自动处理默认值和缺失配置
- **错误处理**：完善的异常处理机制

### 未来版本规划
- **配置UI界面**：自动生成配置界面
- **热重载支持**：配置文件修改后自动重载
- **网络同步**：服务器-客户端配置同步
- **配置模板**：预定义的配置模板系统

## 贡献指南

欢迎为Deicore配置系统贡献代码！请遵循以下指南：

1. **代码风格**：遵循Java编码规范，使用4空格缩进
2. **测试覆盖**：新功能需要包含单元测试
3. **文档更新**：修改代码时需要同步更新文档
4. **向后兼容**：确保修改不会破坏现有功能

## 技术支持

如果在使用过程中遇到问题，可以通过以下方式获取帮助：

1. **查看日志**：Minecraft日志文件包含详细的错误信息
2. **社区支持**：访问项目GitHub页面提交issue
3. **文档参考**：详细的使用说明和API文档
4. **示例代码**：参考提供的示例配置类