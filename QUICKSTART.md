# 快速启动指南

## 立即运行项目的步骤

### 1. 打开项目

在 Android Studio 中：
```
File -> Open -> 选择此文件夹
c:\Users\Wishtobegood\AndroidStudioProjects\MyApplication5
```

### 2. 等待 Gradle 同步

首次打开时，Android Studio 会自动：
- 下载 Gradle 7.5
- 下载依赖库
- 同步项目

**这可能需要 5-10 分钟**，请耐心等待底部的进度条完成。

### 3. 如果同步失败

尝试以下操作：

#### 方法 A：清理并重建
```
Build -> Clean Project
Build -> Rebuild Project
```

#### 方法 B：清除缓存
```
File -> Invalidate Caches / Restart -> Invalidate and Restart
```

#### 方法 C：手动配置 SDK 路径

编辑 `local.properties` 文件，添加：
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

将 `YourUsername` 替换为你的 Windows 用户名。

### 4. 创建模拟器（如果没有真机）

```
Tools -> Device Manager -> Create Device
```

推荐配置：
- 设备：Pixel 4
- 系统：API 28 (Android 9.0)
- 架构：x86_64

### 5. 运行应用

点击绿色运行按钮 ▶ 或按 `Shift + F10`

---

## 已知问题和解决方案

### 问题 1: Gradle 下载很慢

**解决**: 使用国内镜像

在项目根目录的 `build.gradle` 文件中，将 repositories 改为：

```gradle
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/jcenter' }
        mavenCentral()
    }
}
```

### 问题 2: 找不到 SDK

**解决**: 安装 API 28

```
Tools -> SDK Manager -> SDK Platforms
勾选 "Android 9.0 (Pie)" - API Level 28
点击 Apply 安装
```

### 问题 3: JDK 版本不匹配

**解决**: 使用 Android Studio 自带 JDK

```
File -> Project Structure -> SDK Location
JDK location: 使用嵌入式 JDK
```

### 问题 4: 编译错误 "Cannot resolve symbol"

**解决**: 
1. 点击 `File -> Sync Project with Gradle Files`
2. 等待同步完成
3. 如果仍有问题，执行 `Build -> Clean Project`

---

## 验证项目是否正常

成功运行后，你应该看到：

✅ 主界面显示 "当前阶段: 人员调度 | 第 1 天"  
✅ 底部有四个区域：宿舍、训练模拟器、任务指挥部、待命  
✅ 每个区域显示船员列表  
✅ 顶部有保存、读档、下一阶段、统计按钮  

---

## 测试游戏功能

### 测试 1: 查看船员详情
1. 点击任意船员卡片
2. 应该显示详细信息面板
3. 可以看到 HP、护盾、能量条

### 测试 2: 分配船员
1. 选择一个船员
2. 点击 "宿舍"、"训练"、"任务部" 或 "待命" 按钮
3. 船员应该移动到对应区域

### 测试 3: 训练和休息
1. 选择一个船员
2. 点击 "训练" 按钮 - XP 应该增加
3. 点击 "休息" 按钮 - HP 应该恢复

### 测试 4: 进入下一阶段
1. 点击 "下一阶段" 按钮
2. 应该进入任务选择界面
3. 可以看到任务列表

### 测试 5: 组建小队
1. 在任务选择界面
2. 从 "可用成员" 点击添加到小队
3. 在小队中点击可以移除
4. 点击 "开始任务" 进入战斗

### 测试 6: 战斗系统
1. 进入战斗界面
2. 可以看到己方和敌方单位
3. 点击 "攻击" 进行普通攻击
4. 点击 "技能" 释放职业技能
5. 点击 "结束回合" 切换到敌人行动

### 测试 7: 存档读档
1. 在主界面点击 "保存"
2. 关闭应用
3. 重新打开
4. 点击 "读档"
5. 应该恢复到之前的状态

### 测试 8: 统计数据
1. 点击 "统计" 按钮
2. 应该显示统计页面
3. 可以看到饼图和柱状图

---

## 性能优化建议

如果模拟器运行缓慢：

1. **启用硬件加速**
   - Windows: 启用 Hyper-V 或 Windows Hypervisor Platform
   - Intel CPU: 安装 Intel HAXM

2. **降低模拟器分辨率**
   - 创建新的 AVD 时选择较低分辨率

3. **增加分配内存**
   - AVD 设置中将 RAM 调整为 2048MB 或更高

4. **使用真机测试**
   - 真机通常比模拟器流畅得多

---

## 下一步

项目已经可以在 Android Studio 中正常运行了！

如果需要修改代码或添加功能，请参考：
- `README.md` - 完整的项目文档
- 各个 Java 文件的注释
- Android Studio 的代码提示功能

祝你开发顺利！🎉
