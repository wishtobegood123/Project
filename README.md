# Space Colony: Pioneers - Android 项目

这是一个原生 Android 策略模拟游戏，使用 Java 和 API 28 开发。

## 项目要求

- **Android Studio**: Arctic Fox (2020.3.1) 或更高版本
- **JDK**: Java 8 或更高版本（推荐 JDK 11）
- **Gradle**: 7.5（项目已配置）
- **Android SDK**: API 28 (Android 9.0)
- **最低支持**: Android 5.0 (API 21)

## 在 Android Studio 中打开项目

### 方法一：直接打开（推荐）

1. 启动 Android Studio
2. 点击 "Open" 或 "File" -> "Open"
3. 选择项目文件夹：`c:\Users\Wishtobegood\AndroidStudioProjects\MyApplication5`
4. 等待 Gradle 同步完成（首次可能需要下载依赖，请耐心等待）

### 方法二：从欢迎界面导入

1. 启动 Android Studio
2. 在欢迎界面选择 "Import Project"
3. 选择项目的根目录
4. 等待 Gradle 构建完成

## 首次运行前的准备

### 1. 确保已安装 Android SDK Platform 28

打开 Android Studio：
- 点击 "Tools" -> "SDK Manager"
- 在 "SDK Platforms" 标签页中，勾选 "Android 9.0 (Pie)" - API Level 28
- 点击 "Apply" 安装（如果尚未安装）

### 2. 确保已安装 Build Tools

在 SDK Manager 中：
- 切换到 "SDK Tools" 标签页
- 确保已安装 "Android SDK Build-Tools 29.0.3"
- 如果没有，勾选并安装

### 3. 配置 JDK

- 点击 "File" -> "Project Structure" -> "SDK Location"
- 确保 "JDK location" 指向有效的 JDK 路径
- 推荐使用 Android Studio 自带的 JDK

## 运行项目

### 在模拟器上运行

1. 创建虚拟设备：
   - 点击 "Tools" -> "Device Manager"
   - 点击 "Create Device"
   - 选择一个设备（如 Pixel 4）
   - 选择系统镜像：API 28 (Android 9.0)
   - 完成创建

2. 运行应用：
   - 点击工具栏的绿色运行按钮（▶）
   - 或按 `Shift + F10`
   - 选择刚创建的模拟器

### 在真机上运行

1. 启用开发者选项：
   - 进入手机设置 -> 关于手机
   - 连续点击 "版本号" 7 次

2. 启用 USB 调试：
   - 进入设置 -> 开发者选项
   - 开启 "USB 调试"

3. 连接手机：
   - 使用 USB 线连接手机到电脑
   - 在手机上授权 USB 调试

4. 运行应用：
   - 点击运行按钮
   - 选择你的设备

## 项目结构

```
app/src/main/java/com/example/spacecolonypioneers/
├── manager/          # 游戏管理器
│   ├── CombatManager.java       # 战斗管理
│   ├── CrewManager.java         # 船员管理
│   ├── MissionManager.java      # 任务管理
│   ├── ProgressionManager.java  # 进度管理
│   └── StorageManager.java      # 存档管理
├── model/            # 数据模型
│   ├── enums/        # 枚举类型
│   ├── CrewMember.java          # 船员
│   ├── Enemy.java               # 敌人
│   ├── GameState.java           # 游戏状态
│   └── ...
├── ui/               # 用户界面
│   ├── adapter/      # RecyclerView 适配器
│   ├── view/         # 自定义视图
│   ├── MainActivity.java        # 主活动
│   └── StatisticsActivity.java  # 统计活动
└── util/             # 工具类
    └── GsonProvider.java        # JSON 序列化
```

## 游戏功能

### 核心玩法循环

1. **人员调度 (Scheduling)**
   - 将船员分配到宿舍、训练模拟器或任务指挥部
   - 查看船员详细信息

2. **进度处理 (Progression)**
   - 宿舍恢复 HP
   - 训练模拟器获得 XP
   - 提升等级和能力

3. **任务选择 (Mission Selection)**
   - 浏览随机生成的任务
   - 组建小队（最多 5 人）
   - 查看小队加成

4. **战斗 (Combat)**
   - 回合制战斗系统
   - 基础攻击和技能释放
   - 护盾和伤害计算

### 五个职业

- **医疗兵 (Medic)**: 治疗队友，持久战专家
- **工程师 (Engineer)**: 生成护盾，防御出色
- **士兵 (Soldier)**: 高攻击力，前线战斗
- **侦察兵 (Scout)**: 敏捷行动，削弱敌人
- **指挥官 (Commander)**: 团队核心，鼓舞士气

### 特色功能

✅ 完整的存档/读档系统  
✅ 详细的统计数据可视化  
✅ 小队加成系统  
✅ 随机任务生成  
✅ 受伤机制（无永久死亡）  
✅ 技能系统  
✅ 等级成长系统  

## 常见问题

### Gradle 同步失败

**问题**: Gradle sync failed 或依赖下载失败

**解决方案**:
1. 检查网络连接
2. 点击 "File" -> "Invalidate Caches / Restart"
3. 删除 `.gradle` 文件夹后重新同步
4. 确保使用了正确的 Gradle 版本（7.5）

### SDK 未找到

**问题**: SDK location not found

**解决方案**:
1. 在 `local.properties` 文件中添加：
   ```
   sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
   ```
2. 或在 Android Studio 中配置 SDK 路径

### 编译错误

**问题**: 出现编译错误或找不到类

**解决方案**:
1. 点击 "Build" -> "Clean Project"
2. 然后点击 "Build" -> "Rebuild Project"
3. 确保所有依赖都已正确下载

### 模拟器无法启动

**问题**: AVD 启动失败或很慢

**解决方案**:
1. 确保已启用 Intel HAXM 或 Windows Hypervisor Platform
2. 尝试使用 x86_64 系统镜像
3. 增加分配给模拟器的 RAM

## 技术说明

- **架构**: 单 Activity + ViewFlipper 多页面
- **UI**: XML 布局 + RecyclerView
- **数据存储**: JSON 文件（内部存储）
- **图表**: 自定义 View（柱状图、饼图）
- **战斗**: 回合制，非实时渲染

## 修改记录

最近修复的问题：
1. ✅ 升级 Gradle 从 5.6.4 到 7.5（兼容现代 Android Studio）
2. ✅ 升级 Android Gradle Plugin 从 3.6.4 到 7.4.2
3. ✅ 更新依赖库到兼容版本
4. ✅ 修复 lambda 表达式兼容性（API 28）
5. ✅ 添加必要的 import 语句

## 课程展示建议

1. **演示流程**:
   - 展示主界面的三个区域
   - 点击船员查看详情
   - 演示训练和休息功能
   - 选择任务并组建小队
   - 进行一场战斗
   - 查看统计数据

2. **重点展示**:
   - 完整的游戏循环
   - 五个不同职业的特点
   - 存档/读档功能
   - 统计可视化图表
   - 小队加成系统

3. **代码亮点**:
   - 清晰的分层架构
   - 单例模式管理游戏状态
   - 自定义 View 实现图表
   - JSON 序列化存档
   - 观察者模式的 UI 更新

## 联系方式

如有问题，请检查 Android Studio 的 Logcat 输出以获取详细错误信息。

---

**祝你在 Android Studio 中顺利运行！** 🚀
