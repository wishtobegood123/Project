# 项目配置说明

## 已完成的修复

### 1. Gradle 版本升级
- **从**: Gradle 5.6.4
- **到**: Gradle 7.5
- **原因**: 兼容现代 Android Studio 和 JDK 21

### 2. Android Gradle Plugin 升级
- **从**: 3.6.4
- **到**: 7.4.2
- **原因**: 与 Gradle 7.5 兼容

### 3. 依赖库更新
```gradle
androidx.appcompat: 1.1.0 -> 1.2.0
androidx.recyclerview: 1.1.0 -> 1.2.1
com.google.code.gson: 2.8.6 -> 2.8.9
```

### 4. Java 兼容性修复
- 修复了 StatisticsActivity 中的 lambda 表达式
- 改为匿名内部类以兼容 API 28
- 添加了必要的 import 语句

### 5. 空指针保护
- 在 StatisticsActivity 中添加了 null 检查
- 在 MainActivity 的 updateMissionUI 中添加了 null 检查
- 增强了代码的健壮性

## 项目技术栈

### 核心框架
- **语言**: Java 8
- **最低 SDK**: API 21 (Android 5.0)
- **目标 SDK**: API 28 (Android 9.0)
- **编译 SDK**: API 28

### UI 组件
- **布局**: XML + ConstraintLayout/LinearLayout
- **列表**: RecyclerView
- **自定义视图**: 
  - CombatView (战斗可视化)
  - BarChartView (柱状图)
  - PieChartView (饼图)

### 数据存储
- **格式**: JSON
- **工具**: Gson 2.8.9
- **位置**: 内部存储

### 架构模式
- **单 Activity**: MainActivity
- **页面切换**: ViewFlipper
- **状态管理**: GameState (单例)
- **数据流**: Manager 模式

## 项目模块

### 1. Manager 层 (业务逻辑)
```
manager/
├── CombatManager.java       # 战斗系统
├── CrewManager.java         # 船员管理（训练、休息）
├── MissionManager.java      # 任务生成和完成
├── ProgressionManager.java  # 进度处理（XP、HP恢复）
└── StorageManager.java      # 存档/读档
```

### 2. Model 层 (数据模型)
```
model/
├── enums/                   # 枚举类型
│   ├── Assignment.java     # 分配位置
│   ├── MissionModifier.java # 任务修饰符
│   ├── MissionType.java    # 任务类型
│   ├── Phase.java          # 游戏阶段
│   ├── Profession.java     # 职业
│   └── SkillType.java      # 技能类型
├── CombatLogEntry.java     # 战斗日志
├── CombatState.java        # 战斗状态
├── CrewMember.java         # 船员
├── CrewStatistics.java     # 船员统计
├── Enemy.java              # 敌人
├── GameState.java          # 游戏状态（单例）
├── GameStatistics.java     # 游戏统计
├── Mission.java            # 任务
├── MissionTemplate.java    # 任务模板
├── ProfessionConfig.java   # 职业配置
└── SquadBonus.java         # 小队加成
```

### 3. UI 层 (用户界面)
```
ui/
├── adapter/                # RecyclerView 适配器
│   ├── CombatLogAdapter.java
│   ├── CrewAdapter.java
│   ├── MissionAdapter.java
│   └── SquadAdapter.java
├── view/                   # 自定义视图
│   ├── BarChartView.java
│   ├── CombatView.java
│   └── PieChartView.java
├── MainActivity.java       # 主活动
└── StatisticsActivity.java # 统计活动
```

### 4. Util 层 (工具类)
```
util/
└── GsonProvider.java       # Gson 单例提供者
```

## 游戏系统设计

### 1. 核心循环
```
人员调度 → 进度处理 → 任务选择 → 战斗 → 奖励 → 返回调度
```

### 2. 职业系统
| 职业 | HP | 能量 | 攻击 | 防御 | 技能 |
|------|-----|------|------|------|------|
| 医疗兵 | 80 | 120 | 10 | 5 | 治疗 |
| 工程师 | 90 | 100 | 12 | 10 | 修理 |
| 士兵 | 120 | 80 | 20 | 8 | 狂暴射击 |
| 侦察兵 | 70 | 100 | 14 | 6 | 侦察 |
| 指挥官 | 100 | 110 | 15 | 9 | 鼓舞 |

### 3. 成长系统
- **XP 获取**: 训练、完成任务
- **等级提升**: 每 100 XP 升一级
- **属性成长**: 根据职业有不同的 HP 和攻击成长
- **技能解锁**: 随等级提升

### 4. 战斗系统
- **回合制**: 玩家回合 → 敌人回合
- **攻击方式**: 
  - 普通攻击（无消耗）
  - 职业技能（消耗能量）
- **伤害计算**: 攻击 - 防御，先扣护盾再扣 HP
- **胜利条件**: 击败所有敌人
- **失败处理**: 船员受伤但不会永久死亡

### 5. 小队加成系统
不同的职业组合会触发不同的加成：
- **医疗小组**: 多个医疗兵提高治疗效果
- **工程团队**: 多个工程师提高护盾强度
- **突击小队**: 多个士兵提高攻击力
- **侦察网络**: 多个侦察兵降低敌人命中
- **指挥链**: 指挥官提高全队属性

### 6. 任务系统
- **随机生成**: 每次进入任务选择阶段生成新任务
- **难度分级**: 简单、中等、困难
- **奖励**: XP、进度、碎片
- **修饰符**: 随机效果（如敌人增强、奖励加倍等）

### 7. 存档系统
- **保存内容**: 
  - 所有船员数据（HP、XP、等级、位置）
  - 游戏进度（天数、阶段、资源）
  - 统计数据
  - 当前选中的船员和任务
- **存储格式**: JSON
- **存储位置**: 应用内部存储

### 8. 统计系统
记录并可视化：
- 总任务数、胜利数、成功率
- 训练次数
- 总 XP 获得
- 总碎片收集
- 每个船员的：
  - 出战次数
  - 总伤害输出
  - 总治疗量
  - 承受伤害

## 文件说明

### 关键配置文件

#### build.gradle (项目级)
```gradle
classpath 'com.android.tools.build:gradle:7.4.2'
```

#### app/build.gradle (应用级)
```gradle
compileSdkVersion 28
minSdkVersion 21
targetSdkVersion 28

dependencies {
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'androidx.recyclerview:recyclerview:1.2.1'
    implementation 'com.google.code.gson:gson:2.8.9'
}
```

#### gradle-wrapper.properties
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-7.5-all.zip
```

### 布局文件

#### activity_main.xml
主界面布局，包含：
- 顶部信息栏（阶段、资源）
- 控制按钮（保存、读档、下一阶段、统计）
- ViewFlipper（三个页面）
  - 页面 1: 人员调度（四个区域 + 船员详情）
  - 页面 2: 任务选择（任务列表 + 小队组建）
  - 页面 3: 战斗界面（CombatView + 控制按钮 + 战斗日志）

#### activity_statistics.xml
统计页面布局，包含：
- 总体统计数据
- 任务成功率饼图
- 伤害输出柱状图
- 治疗量柱状图
- 出战次数柱状图

#### item_crew.xml
船员列表项布局

#### item_mission.xml
任务列表项布局

#### item_squad.xml
小队成员列表项布局

#### item_combat_log.xml
战斗日志项布局

## 运行环境要求

### 开发环境
- **Android Studio**: Arctic Fox (2020.3.1) 或更高
- **JDK**: 8 或更高（推荐 11）
- **Gradle**: 7.5
- **Android SDK**: 
  - Platform: API 28
  - Build Tools: 29.0.3

### 运行环境
- **最低**: Android 5.0 (API 21)
- **推荐**: Android 9.0 (API 28) 或更高
- **内存**: 至少 2GB RAM
- **存储**: 至少 100MB 可用空间

## 常见问题排查

### 编译问题

**Q: Gradle sync 失败**
A: 
1. 检查网络连接
2. 清除缓存：File -> Invalidate Caches / Restart
3. 删除 .gradle 文件夹重新同步

**Q: 找不到 SDK**
A: 
1. 打开 SDK Manager
2. 安装 API 28 Platform
3. 安装 Build Tools 29.0.3

**Q: 类找不到**
A:
1. Sync Project with Gradle Files
2. Clean Project
3. Rebuild Project

### 运行时问题

**Q: 应用闪退**
A:
1. 查看 Logcat 错误信息
2. 检查是否有 NullPointerException
3. 确认 GameState 已正确初始化

**Q: 存档无法读取**
A:
1. 检查文件权限
2. 确认 JSON 格式正确
3. 查看 StorageManager 的异常日志

**Q: 模拟器卡顿**
A:
1. 启用硬件加速（HAXM 或 Hyper-V）
2. 增加模拟器 RAM
3. 使用 x86_64 镜像
4. 考虑使用真机测试

## 扩展建议

如果需要进一步改进项目：

1. **添加 Fragment**
   - 将三个页面拆分为独立的 Fragment
   - 使用 Navigation Component 管理页面跳转

2. **ViewModel + LiveData**
   - 使用 ViewModel 管理游戏状态
   - 使用 LiveData 实现响应式 UI 更新

3. **Room Database**
   - 替换 JSON 存档为 Room 数据库
   - 支持更复杂的数据查询

4. **动画效果**
   - 添加页面切换动画
   - 添加战斗特效
   - 添加数值变化动画

5. **音效和音乐**
   - 添加背景音乐
   - 添加战斗音效
   - 添加 UI 反馈音

6. **更多职业和技能**
   - 扩展职业系统
   - 添加技能树
   - 添加装备系统

7. **多人合作**
   - 添加在线排行榜
   - 添加好友系统
   - 添加公会功能

---

**项目已经过优化，可以直接在 Android Studio 中运行！**

如有任何问题，请查看 README.md 和 QUICKSTART.md 获取帮助。
