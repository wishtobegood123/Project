# 快速修复：安装 JDK 17 解决兼容性问题

## 🔍 当前问题

- 你的系统只有 Java 21
- 没有找到 Android Studio 的嵌入式 JDK
- AGP 7.4.2 不支持 Java 21

## ✅ 最佳解决方案：安装 JDK 17

### 步骤 1：下载 JDK 17

选择以下任一方式下载：

#### 选项 A：Adoptium Temurin（推荐，免费开源）
1. 访问：https://adoptium.net/temurin/releases/?version=17
2. 选择：
   - Operating System: **Windows**
   - Architecture: **x64**
   - Package Type: **JDK** (.msi)
3. 点击下载

#### 选项 B：Oracle JDK（需要注册）
1. 访问：https://www.oracle.com/java/technologies/downloads/#java17
2. 选择 Windows x64 Installer
3. 下载 .exe 文件

#### 选项 C：Amazon Corretto（AWS 维护）
1. 访问：https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jdk.msi
2. 直接下载 .msi 安装包

### 步骤 2：安装 JDK 17

1. 运行下载的安装程序
2. 使用默认设置安装
3. **重要**：记住安装路径，通常是：
   ```
   C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot\
   或
   C:\Program Files\Java\jdk-17\
   ```

### 步骤 3：验证安装

打开新的 PowerShell 窗口（**必须新开窗口**）：

```powershell
# 检查是否安装了多个 Java 版本
Get-ChildItem "C:\Program Files" -Directory | Where-Object { $_.Name -like "*java*" -or $_.Name -like "*jdk*" -or $_.Name -like "*eclipse*" } | Select-Object FullName
```

你应该能看到新安装的 JDK 17 目录。

### 步骤 4：配置项目使用 JDK 17

#### 方法 A：在 Android Studio 中配置（推荐）

1. **打开 Android Studio**
2. **打开 Project Structure**
   - 按 `Ctrl + Alt + Shift + S`
   - 或 File -> Project Structure

3. **配置 SDK Location**
   - 左侧选择：**SDK Location**
   - 找到 **JDK location**
   - 点击下拉菜单或 "..." 按钮
   - 浏览到 JDK 17 的安装路径，例如：
     ```
     C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot
     ```
   - 点击 **OK**

4. **同步项目**
   - 点击右上角的 **Sync Now** 按钮

5. **清理并重新构建**
   ```
   Build -> Clean Project
   Build -> Rebuild Project
   ```

#### 方法 B：通过命令行临时测试

如果你想在命令行中快速测试，可以这样做：

```powershell
# 1. 找到 JDK 17 的路径（根据你的实际安装位置修改）
$JAVA_17_PATH = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot"

# 2. 设置环境变量
$env:JAVA_HOME = $JAVA_17_PATH
$env:PATH = "$JAVA_17_PATH\bin;$env:PATH"

# 3. 验证 Java 版本
java -version
# 应该显示：openjdk version "17.x.x"

# 4. 清理并构建
cd c:\Users\Wishtobegood\AndroidStudioProjects\MyApplication5
.\gradlew clean
.\gradlew assembleDebug
```

**注意**：这个方法只对当前终端窗口有效。

### 步骤 5：永久配置环境变量（可选）

如果你希望系统默认使用 JDK 17：

```powershell
# 以管理员身份运行 PowerShell

# 1. 设置 JAVA_HOME
[System.Environment]::SetEnvironmentVariable(
    "JAVA_HOME", 
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot", 
    "Machine"
)

# 2. 更新 PATH（将 JDK 17 放在前面）
$currentPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
$newPath = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot\bin;" + $currentPath
[System.Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")

# 3. 重启终端后验证
java -version
```

---

## ⚡ 快速检查清单

安装完成后，请确认：

- [ ] JDK 17 已安装
- [ ] 能找到 JDK 17 的安装路径
- [ ] 在 Android Studio 中配置了 JDK 17
- [ ] 执行了 Sync Project with Gradle Files
- [ ] 执行了 Clean Project
- [ ] 执行了 Rebuild Project
- [ ] 构建成功，没有 NullPointerException 错误

---

## 🔧 如果仍然有问题

### 问题 1：找不到 JDK 17 安装路径

运行以下命令查找：

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
        Get-ChildItem $loc -Directory | Where-Object { $_.Name -like "*jdk*" -or $_.Name -like "*17*" } | Select-Object FullName
    }
}
```

### 问题 2：Android Studio 中无法选择 JDK 17

1. 确保 JDK 17 已正确安装
2. 在 Project Structure 中点击 "Add JDK..."
3. 手动浏览到 JDK 17 的根目录
4. 确保选择的是包含 `bin`、`lib` 等文件夹的根目录

### 问题 3：构建时仍然使用 Java 21

1. 关闭所有 PowerShell/终端窗口
2. 重新启动 Android Studio
3. File -> Invalidate Caches / Restart
4. 重新打开项目
5. 检查 Project Structure 中的 JDK 设置

---

## 📝 验证成功

构建成功后，你应该看到：

```
BUILD SUCCESSFUL in XXs
XX actionable tasks: XX executed
```

并且生成 APK：
```
app\build\outputs\apk\debug\app-debug.apk
```

---

## 💡 提示

- **推荐使用 Adoptium Temurin**：免费、开源、稳定
- **安装后重启 Android Studio**：确保配置生效
- **保留 Java 21**：不需要卸载，只是这个项目使用 JDK 17
- **其他项目可以使用不同 JDK**：每个项目可以独立配置

---

*创建日期: 2026年4月18日*  
*目标: 通过安装 JDK 17 解决 AGP 7.4.2 兼容性问题*
