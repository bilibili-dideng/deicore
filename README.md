# deicore

This is a Fabric mod for Minecraft, which adds commands such as WorldVar and some APIs

## How to use it as a dependency on my module?

Add the following to your `build.gradle` file:

```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    implementation 'com.github.bilibili-dideng:deicore:v1.0'
}
```

If you are an IDEA user, please add the deicore mod (not source code!) in File -> Project Structure -> Libraries