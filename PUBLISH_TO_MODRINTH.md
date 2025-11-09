# 发布到 Modrinth Maven 仓库指南

## 配置说明

我已经为您的 Deicore 模组配置了 Modrinth Maven 仓库发布功能。以下是配置的详细信息：

### 1. 构建配置 (build.gradle)

在 `publishing` 部分添加了 Modrinth Maven 仓库配置：

```gradle
repositories {
    // Modrinth Maven 仓库
    maven {
        name = "modrinth"
        url = "https://api.modrinth.com/maven"
        credentials {
            username = project.findProperty("modrinth.token") ?: System.getenv("MODRINTH_TOKEN")
            password = ""
        }
    }
}
```

### 2. 发布步骤

#### 第一步：获取 Modrinth API Token

1. 访问 [Modrinth](https://modrinth.com)
2. 登录您的账户
3. 进入 Settings → API Tokens
4. 创建一个新的 API Token（需要 "Upload version" 权限）

#### 第二步：设置环境变量或 Gradle 属性

**方法一：环境变量（推荐）**
```bash
# Windows PowerShell
$env:MODRINTH_TOKEN="your_api_token_here"

# Windows CMD
set MODRINTH_TOKEN=your_api_token_here

# Linux/macOS
export MODRINTH_TOKEN=your_api_token_here
```

**方法二：Gradle 属性文件**
在项目根目录创建 `gradle.properties` 文件（如果不存在）：
```properties
modrinth.token=your_api_token_here
```

#### 第三步：发布到 Modrinth

```bash
# 构建并发布模组
./gradlew build publish

# 或者分别执行
./gradlew build
./gradlew publish
```

### 3. 发布后的访问地址

发布成功后，您的模组将在以下地址可用：

```
https://api.modrinth.com/maven/org/dideng/com/deicore/<version>/deicore-<version>.jar
```

示例（假设版本为 1.0.0）：
```
https://api.modrinth.com/maven/org/dideng/com/deicore/1.0.0/deicore-1.0.0.jar
```

### 4. 在其他项目中使用

其他开发者可以在他们的 `build.gradle` 中添加依赖：

```gradle
repositories {
    maven {
        name = "modrinth"
        url = "https://api.modrinth.com/maven"
    }
}

dependencies {
    modImplementation "org.dideng.com:deicore:1.0.0"
}
```

### 5. 版本管理

确保每次发布时更新 `gradle.properties` 中的版本号：

```properties
mod_version=1.0.1
```

### 6. 验证发布

发布后，您可以通过以下方式验证：

1. 访问 Modrinth 网站查看您的模组页面
2. 使用浏览器直接访问 Maven 仓库 URL
3. 在其他项目中测试依赖是否正常工作

### 7. 注意事项

- **API Token 安全**：不要将 API Token 提交到版本控制系统
- **版本冲突**：确保每次发布使用唯一的版本号
- **依赖关系**：确保所有依赖项都已正确配置
- **许可证**：确保您的模组有合适的许可证文件

### 8. 故障排除

**常见问题：**

1. **认证失败**：检查 API Token 是否正确设置
2. **版本已存在**：使用新的版本号重新发布
3. **网络问题**：检查网络连接和防火墙设置
4. **权限不足**：确保 API Token 有足够的权限

**调试命令：**
```bash
# 查看详细的错误信息
./gradlew publish --stacktrace

# 查看构建配置
./gradlew properties
```

## 支持

如果您在发布过程中遇到问题，请检查：
- Modrinth 官方文档：https://docs.modrinth.com
- Gradle 发布文档：https://docs.gradle.org/current/userguide/publishing_maven.html
- Fabric Loom 文档：https://fabricmc.net/wiki/documentation:loom