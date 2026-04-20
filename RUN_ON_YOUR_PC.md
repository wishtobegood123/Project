# 在您的电脑上直接运行项目 - 完整指南

## 🎯 目标

让您的项目能够在当前电脑上成功编译和运行。

---

## 🔍 当前问题分析

### 已确认的环境：
- ✅ **Android SDK**：`C:\Users\Wishtobegood\AppData\Local\Android\Sdk`
- ✅ **Java 21**：`C:\Program Files\Java\jdk-21`
- ❌ **没有找到 Android Studio 的嵌入式 JDK**
- ⚠️ **Gradle 版本**：刚刚从 8.9 改回 7.5
- ⚠️ **AGP 版本**：7.4.2

### 核心问题：

```
❌ Java 21 + AGP 7.4.2 = 不兼容
```

**错误表现：**
```
java.lang.NullPointerException
Failed to process: ...\enums\MissionType.class, Phase.class, etc.
DexArchiveBuilderException
```

---

## ✅ 解决方案（按优先级排序）

### 🥇 方案 1：安装 JDK 17（强烈推荐）⭐⭐⭐

这是**最简单、最可靠**的解决方案。

#### 详细步骤：

##### 1. 下载 JDK 17

访问以下任一网站下载：

**推荐：Adoptium Temurin（免费开源）**
```
https://adoptium.net/temurin/releases/?version=17
```

选择：
- Operating System: **Windows**
- Architecture: **x64**
- Package Type: **JDK** (.msi)

点击下载并等待完成。

##### 2. 安装 JDK 17

1. 双击下载的 `.msi` 文件
2. 使用默认设置安装
3. **重要**：记住安装路径，通常是：
   ```
   C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot
   ```
   
   或者可能是：
   ```
   C:\Program Files\Java\jdk-17
   ```

##### 3. 验证安装

打开**新的** PowerShell 窗口（必须新开）：

```powershell
# 查找 JDK 17 的安装位置
Get-ChildItem "C:\Program Files" -Directory | Where-Object { 
    $_.Name -like "*jdk*" -or $_.Name -like "*eclipse*" -or $_.Name -like "*adoptium*" 
} | Select-Object FullName
```

你应该能看到类似这样的输出：
```
FullName
--------
C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot
```

##### 4. 在 Android Studio 中配置 JDK 17

1. **打开 Android Studio**
   - 如果已经打开，先关闭再重新打开

2. **打开 Project Structure**
   - 按快捷键：`Ctrl + Alt + Shift + S`
   - 或菜单：File -> Project Structure

3. **配置 JDK**
   - 在左侧面板选择：**SDK Location**
   - 找到右侧的 **JDK location** 部分
   - 点击下拉菜单或 "..." 按钮
   - 浏览到 JDK 17 的安装路径
   - 例如：`C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot`
   - 点击 **OK**

4. **应用更改**
   - 点击 **Apply**
   - 点击 **OK**

##### 5. 同步项目

1. **Sync Project with Gradle Files**
   - 点击右上角的 **"Sync Now"** 按钮
   - 或菜单：File -> Sync Project with Gradle Files
   - 等待同步完成（可能需要几分钟）

##### 6. 清理并重新构建

1. **Clean Project**
   ```
   Build -> Clean Project
   ```
   等待清理完成。

2. **Rebuild Project**
   ```
   Build -> Rebuild Project
   ```
   等待构建完成。

##### 7. 运行应用

1. **连接设备或启动模拟器**
   - 连接真实的 Android 手机（开启 USB 调试）
   - 或在 Android Studio 中启动模拟器

2. **运行应用**
   - 点击工具栏的绿色运行按钮 ▶
   - 或菜单：Run -> Run 'app'
   - 选择目标设备

3. **等待安装和启动**
   - 应用会自动安装到设备上
   - 自动启动

---

### 🥈 方案 2：通过命令行临时测试（快速验证）

如果你想在安装 JDK 17 之前快速测试，可以使用这个方法。

#### 步骤：

##### 1. 先安装 JDK 17（同上）

##### 2. 在 PowerShell 中设置临时环境变量

```powershell
# 1. 设置 JAVA_HOME（根据你的实际安装路径修改）
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot"

# 2. 更新 PATH
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# 3. 验证 Java 版本
java -version
# 应该显示：openjdk version "17.x.x"
```

##### 4. 清理并构建

```powershell
cd c:\Users\Wishtobegood\AndroidStudioProjects\MyApplication5

# 清理
.\gradlew clean

# 构建
.\gradlew assembleDebug
```

##### 5. 检查是否成功

如果看到：
```
BUILD SUCCESSFUL in XXs
```

说明成功了！APK 文件位于：
```
app\build\outputs\apk\debug\app-debug.apk
```

**注意：** 这个方法只对当前终端窗口有效，关闭窗口后需要重新设置。

---

### 🥉 方案 3：永久配置系统环境变量

如果你希望整个系统都使用 JDK 17：

#### 步骤：

##### 1. 以管理员身份运行 PowerShell

右键 PowerShell -> 以管理员身份运行

##### 2. 设置 JAVA_HOME

```powershell
# 设置 JAVA_HOME（根据实际路径修改）
[System.Environment]::SetEnvironmentVariable(
    "JAVA_HOME", 
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot", 
    "Machine"
)
```

##### 3. 更新 PATH

```powershell
# 获取当前 PATH
$currentPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")

# 将 JDK 17 添加到 PATH 前面
$newPath = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot\bin;" + $currentPath

# 设置新的 PATH
[System.Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
```

##### 4. 重启所有终端

关闭所有 PowerShell/CMD 窗口，重新打开。

##### 5. 验证

```powershell
java -version
# 应该显示：openjdk version "17.x.x"
```

---

## 📋 验证清单

完成上述任一方后，请确认：

- [ ] JDK 17 已成功安装
- [ ] 能找到 JDK 17 的安装路径
- [ ] `java -version` 显示版本 17.x.x
- [ ] Android Studio 中配置了 JDK 17
- [ ] 执行了 Sync Project with Gradle Files
- [ ] 执行了 Clean Project
- [ ] 执行了 Rebuild Project
- [ ] 构建成功，没有错误
- [ ] APK 文件已生成：`app\build\outputs\apk\debug\app-debug.apk`
- [ ] 应用能在设备/模拟器上运行

---

## 🔧 故障排除

### 问题 1：找不到 JDK 17 安装路径

**症状：** 安装后找不到 JDK 17 在哪里

**解决：**
```powershell
# 搜索所有可能的 JDK 位置
$locations = @(
    "C:\Program Files\Java",
    "C:\Program Files\Eclipse Adoptium",
    "C:\Program Files\Amazon Corretto",
    "C:\Program Files (x86)\Java",
    "$env:LOCALAPPDATA\Programs"
)

foreach ($loc in $locations) {
    if (Test-Path $loc) {
        Write-Host "`nFound in: $loc"
        Get-ChildItem $loc -Directory | Where-Object { 
            $_.Name -like "*jdk*" -or $_.Name -like "*17*" 
        } | Select-Object FullName
    }
}
```

### 问题 2：Android Studio 中无法选择 JDK 17

**症状：** 在 Project Structure 中看不到 JDK 17

**解决：**
1. 确保 JDK 17 已正确安装
2. 点击 "Add JDK..." 或 "..." 按钮
3. 手动浏览到 JDK 17 的根目录
4. 确保选择的是包含 `bin`、`lib` 等文件夹的根目录

### 问题 3：构建时仍然出现 NullPointerException

**症状：** 配置了 JDK 17 但仍然报错

**解决：**
1. **完全清理缓存**
   ```
   File -> Invalidate Caches / Restart
   -> Invalidate and Restart
   ```

2. **删除构建目录**
   ```powershell
   cd c:\Users\Wishtobegood\AndroidStudioProjects\MyApplication5
   Remove-Item -Recurse -Force .gradle
   Remove-Item -Recurse -Force app\build
   ```

3. **重新同步**
   ```
   File -> Sync Project with Gradle Files
   ```

4. **重新构建**
   ```
   Build -> Rebuild Project
   ```

### 问题 4：Gradle 同步失败

**症状：** Sync Project 时出错

**解决：**
1. 检查网络连接
2. 检查 Gradle 版本是否正确（应该是 7.5）
3. 查看 `gradle-wrapper.properties`：
   ```properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-7.5-all.zip
   ```

### 问题 5：找不到 Android SDK

**症状：** 提示找不到 Android SDK

**解决：**
1. 打开 Project Structure
2. 选择 SDK Location
3. 确认 Android SDK 路径为：
   ```
   C:\Users\Wishtobegood\AppData\Local\Android\Sdk
   ```
4. 如果不对，点击 "..." 浏览到正确路径

---

## 💡 重要提示

### 1. Gradle 版本已修复

我已经将 Gradle 版本从 8.9 改回了 7.5，与 AGP 7.4.2 兼容。

**当前配置：**
- AGP: 7.4.2
- Gradle: 7.5
- JDK: 需要使用 17（你当前是 21，不兼容）

### 2. 不需要卸载 Java 21

你可以保留 Java 21，只是这个项目使用 JDK 17。其他项目可以继续使用 Java 21。

### 3. Android Studio 的嵌入式 JDK

如果你的 Android Studio 版本较新，它可能自带 JDK 11 或 17。

**查找方法：**
```
Help -> About
查看 Android Studio 的安装路径
然后检查该路径下是否有 jbr 或 jre 文件夹
```

常见路径：
```
C:\Program Files\Android\Android Studio\jbr
C:\Program Files\Android\Android Studio\jre
```

如果有，可以直接在 Project Structure 中选择 "Embedded JDK"。

---

## 🎯 推荐的执行顺序

### 最快路径（约 10-15 分钟）：

1. **下载并安装 JDK 17**（5 分钟）
   - https://adoptium.net/temurin/releases/?version=17

2. **在 Android Studio 中配置**（2 分钟）
   - Ctrl + Alt + Shift + S
   - SDK Location -> JDK location
   - 选择 JDK 17 路径

3. **同步项目**（3-5 分钟）
   - 点击 Sync Now

4. **清理并构建**（2-3 分钟）
   - Build -> Clean Project
   - Build -> Rebuild Project

5. **运行应用**（1 分钟）
   - 点击运行按钮 ▶

---

## 📊 成功标志

### 构建成功：

你会看到：
```
BUILD SUCCESSFUL in XXs
XX actionable tasks: XX executed
```

### APK 生成：

文件位于：
```
app\build\outputs\apk\debug\app-debug.apk
```

### 应用运行：

- 应用安装到设备/模拟器
- 自动启动
- 可以看到游戏界面

---

## ❓ 常见问题

### Q1: 为什么要安装 JDK 17？不能用 Java 21 吗？

**A:** AGP 7.4.2 内置的编译器不支持 Java 21。虽然可以升级 AGP 到 8.x 来支持 Java 21，但这需要大量代码修改，不符合课程要求。

### Q2: 安装 JDK 17 会影响其他项目吗？

**A:** 不会。每个项目可以在 Android Studio 中独立配置 JDK 版本。其他项目可以继续使用 Java 21。

### Q3: 我可以用命令行的方式吗？不用 Android Studio？

**A:** 可以，但需要先安装 JDK 17，然后设置环境变量，最后用 `gradlew assembleDebug` 构建。但调试和运行还是需要 Android Studio 或模拟器。

### Q4: 如果我不想安装 JDK 17 怎么办？

**A:** 那就无法在当前环境下构建项目。唯一的替代方案是：
- 找一台有 JDK 11/17 的电脑
- 或者升级 AGP 到 8.x（需要大量修改代码）

### Q5: 课程要求 API 28，这会影响什么吗？

**A:** 不会影响。API 28 是 Android 的版本，与 JDK 版本无关。我们只是需要合适的 JDK 来编译代码。

---

## 📝 总结

**要让项目在你的电脑上运行，你需要：**

1. ✅ **安装 JDK 17**（必需）
2. ✅ **在 Android Studio 中配置 JDK 17**（必需）
3. ✅ **同步并构建项目**（必需）
4. ✅ **运行应用**（必需）

**我已经帮你做的：**
- ✅ 将 Gradle 从 8.9 改回 7.5
- ✅ 创建了详细的安装和配置指南
- ✅ 提供了故障排除方案

**接下来你需要做的：**
1. 下载并安装 JDK 17
2. 在 Android Studio 中配置
3. 同步、构建、运行

---

*创建日期: 2026年4月18日*  
*目的: 提供在用户电脑上运行项目的完整指南*  
*状态: Gradle 版本已修复，等待用户安装 JDK 17*
