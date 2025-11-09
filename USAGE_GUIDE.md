# Deicore 模组使用指南

## 📦 作为依赖使用

其他开发者可以将 Deicore 模组作为前置模组来使用。

### 方法1：GitHub Packages（推荐）

在您的 `build.gradle` 中添加：

```gradle
repositories {
    maven {
        name = "DeicoreGitHubPackages"
        url = "https://maven.pkg.github.com/dideng/deicore"
    }
}

dependencies {
    modImplementation "org.dideng.com:deicore:1.0"
}
```

### 方法2：本地构建（开发时）

如果您在本地开发，可以这样引用：

```gradle
dependencies {
    modImplementation files("path/to/deicore-1.0.jar")
}
```

### 方法3：JitPack（备选）

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation 'com.github.dideng:deicore:1.0'
}
```

## 🚀 发布流程

### 发布到 GitHub Packages

1. 设置 GitHub Token：
   ```bash
   # 在 gradle-local.properties 中设置
   gpr.user=your_github_username
   gpr.token=ghp_your_github_token
   ```

2. 发布命令：
   ```bash
   ./gradlew build publish
   ```

### 发布到 Modrinth（手动）

1. 构建模组：
   ```bash
   ./gradlew build
   ```

2. 访问 [Modrinth](https://modrinth.com) 网站
3. 上传 `build/libs/deicore-1.0.jar` 文件

## 🔧 API 使用示例

### 在代码中引用 Deicore

```java
import org.dideng.deicore.api.DeicoreAPI;

public class YourMod {
    public void useDeicore() {
        // 使用 Deicore 提供的功能
        DeicoreAPI.someMethod();
    }
}
```

### fabric.mod.json 配置

```json
{
    "depends": {
        "deicore": "^1.0"
    }
}
```

## 📋 版本管理

- **主版本号**：不兼容的 API 更改
- **次版本号**: 向后兼容的功能性新增
- **修订号**：向后兼容的问题修正

## ❓ 常见问题

### Q: 如何获取 GitHub Token？
A: 访问 GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)

### Q: 发布失败怎么办？
A: 检查 Token 权限是否包含 "write:packages" 和 "read:packages"

### Q: 其他开发者如何使用？
A: 他们需要在 GitHub 上 fork 项目或使用发布的版本

## 📞 支持

如有问题，请提交 Issue 或联系维护者。