# 游戏机制优化 - 每日分配系统

## 🎯 核心改进

### 1. **待命船员显示在最上方** ✅
- 将"待命船员"列表移到调度阶段的最顶部
- 方便玩家快速查看所有可分配的船员
- 添加醒目标题："📋 待命船员（点击选择后分配地点）"

### 2. **移除无限训练/休息问题** ✅
- ❌ 删除了"训练"和"休息"按钮
- ✅ 改为每天分配一次，自动生效
- ✅ 防止重复分配（已分配的船员无法再次分配）

### 3. **明确的三选一分配机制** ✅
- 玩家从待命列表中选择船员
- 点击三个按钮之一进行分配：
  - 🏠 **宿舍** - 恢复HP和能量
  - 💻 **训练模拟器** - 获得经验值
  - ⚔️ **任务指挥部** - 准备参加战斗
- 每个船员每天只能选择一个地点

---

## 📝 修改的文件清单

### 1. **activity_main.xml** - 主界面布局重构
**主要变化：**

#### 布局顺序调整（从上到下）：
```
1. 📋 待命船员列表（最上方）
   - 招募按钮
   - rvUnassigned (RecyclerView)

2. 🎯 分配去向（三选一按钮）
   - 🏠 宿舍 (恢复HP/能量)
   - 💻 训练 (获得经验)
   - ⚔️ 任务部 (准备战斗)

3. 船员详情面板
   - 显示选中船员的详细信息
   - 移除了训练/休息按钮

4. 已分配的船员列表
   - 🏠 宿舍
   - 💻 训练模拟器
   - ⚔️ 任务指挥部
```

**具体修改：**
- 移除了 `btnAssignUnassigned` 按钮（不再需要"分配到待命"）
- 移除了 `btnTrain` 和 `btnRest` 按钮
- 将待命区域移到最上方
- 调整按钮文本，增加emoji和说明
- 减小已分配列表的高度（140dp → 120dp）
- 增大待命列表的高度（140dp → 160dp）

---

### 2. **MainActivity.java** - 核心逻辑修改

#### a) 移除不需要的成员变量
```java
// 删除
private Button btnTrain, btnRest;
```

#### b) 移除不需要的视图引用
```java
// 删除
btnTrain = findViewById(R.id.btnTrain);
btnRest = findViewById(R.id.btnRest);
Button btnAssignUnassigned = findViewById(R.id.btnAssignUnassigned);
```

#### c) 移除训练/休息按钮的点击事件
```java
// 删除了整个 btnTrain.setOnClickListener 和 btnRest.setOnClickListener
```

#### d) 增强 assignListener - 防止重复分配
```java
private View.OnClickListener assignListener(final Assignment assignment) {
    return new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (gameState.getSelectedCrew() != null) {
                CrewMember selected = gameState.getSelectedCrew();
                
                // ✅ 检查是否已经分配过
                if (selected.getAssignment() != Assignment.UNASSIGNED) {
                    Toast.makeText(MainActivity.this, 
                        selected.getName() + " 已经分配到" + getAssignmentName(selected.getAssignment()) + 
                        "，无法重复分配！", 
                        Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // ✅ 分配去向并显示提示
                selected.setAssignment(assignment);
                String locationName = getAssignmentName(assignment);
                Toast.makeText(MainActivity.this, 
                    selected.getName() + " 已分配到 " + locationName, 
                    Toast.LENGTH_LONG).show();
                updateUI();
            } else {
                // ✅ 未选择船员时的提示
                Toast.makeText(MainActivity.this, 
                    "请先从待命列表中选择一名船员", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    };
}
```

#### e) 新增辅助方法
```java
private String getAssignmentName(Assignment assignment) {
    switch (assignment) {
        case QUARTERS: return "宿舍";
        case SIMULATOR: return "训练模拟器";
        case MISSION_CONTROL: return "任务指挥部";
        default: return "待命";
    }
}
```

#### f) 移除 updateCrewDetail 中的按钮控制
```java
// 删除了以下代码：
boolean hasActions = selected.hasActionsRemaining();
btnTrain.setEnabled(hasActions);
btnRest.setEnabled(hasActions);
```

---

### 3. **ProgressionManager.java** - 添加重置逻辑

**关键修改：**
```java
// 在处理完所有船员的效果后，重置分配状态
for (CrewMember crew : crewList) {
    if (crew != null) {
        crew.setAssignment(Assignment.UNASSIGNED);
    }
}

state.setDay(state.getDay() + 1);
```

**作用：**
- 每天开始时，所有船员回到"待命"状态
- 玩家可以重新为每个船员分配新的去向
- 实现了"每天三选一"的核心机制

---

### 4. **CrewAdapter.java** - 优化显示逻辑

**修改内容：**
- 优化了船员名称的显示逻辑
- 保持战斗冷却信息的显示
- 代码结构更清晰，添加了注释

---

## 🎮 新的游戏流程

### 人员调度阶段（SCHEDULING）

```
第1步：查看待命船员列表（最上方）
┌─────────────────────────────┐
│ 📋 待命船员                 │
│ ┌─────────────────────────┐ │
│ │ 👥 招募新船员           │ │
│ ├─────────────────────────┤ │
│ │ Anna (MEDIC Lv.1)      │ │ ← 点击选择
│ │ Bob (ENGINEER Lv.1)    │ │
│ │ Chris (SOLDIER Lv.1)   │ │
│ └─────────────────────────┘ │
└─────────────────────────────┘

第2步：选择船员后，点击分配按钮
┌─────────────────────────────┐
│ 🎯 分配去向（三选一）       │
│ ┌──────┬──────┬──────┐     │
│ │ 🏠   │ 💻   │ ⚔️   │     │
│ │宿舍  │训练  │任务部│     │
│ └──────┴──────┴──────┘     │
└─────────────────────────────┘

第3步：查看已分配的船员
┌─────────────────────────────┐
│ 🏠 宿舍 (恢复HP/能量)       │
│ ┌─────────────────────────┐ │
│ │ Anna (已分配)           │ │
│ └─────────────────────────┘ │
│                             │
│ 💻 训练模拟器 (获得经验)    │
│ ┌─────────────────────────┐ │
│ │ Bob (已分配)            │ │
│ └─────────────────────────┘ │
│                             │
│ ⚔️ 任务指挥部 (准备战斗)    │
│ ┌─────────────────────────┐ │
│ │ Chris (已分配)          │ │
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

### 进展阶段（PROGRESSION）

点击"下一阶段"后，自动执行：

1. **宿舍船员**：
   - HP +20
   - 能量 +25
   - 如果有医生在宿舍：额外HP +15
   - 受伤船员HP >= 60%时痊愈

2. **训练船员**：
   - XP +20
   - 能量 -10
   - 可能升级

3. **所有船员**：
   - 战斗冷却天数 -1
   - 能量自然恢复 +5

4. **重置分配**：
   - 所有船员回到"待命"状态
   - 天数 +1

### 任务选择阶段（MISSION_SELECTION）

- 从任务指挥部选择最多5名船员
- **只能选择冷却完成的船员**
- 开始战斗

---

## 🔍 关键设计决策

### 1. 为什么移除"分配到待命"按钮？
- **简化操作**：不需要手动取消分配
- **自动重置**：每天开始时自动回到待命状态
- **防止混乱**：避免玩家误操作

### 2. 为什么防止重复分配？
- **游戏平衡**：一个船员不能同时在两个地方
- **策略深度**：玩家需要权衡每个船员的去向
- **真实感**：一个人不能分身

### 3. 为什么在进展阶段重置分配？
- **每日循环**：符合"每天重新规划"的设计理念
- **灵活性**：玩家可以根据情况调整策略
- **清晰度**：每天都从相同的起点开始

### 4. 为什么待命列表放在最上方？
- **可见性**：首先看到可操作的船员
- **工作流程**：选择 → 分配，符合直觉
- **效率**：减少滚动操作

---

## 🧪 测试建议

### 1. 测试分配流程
1. 启动游戏，进入调度阶段
2. 确认待命列表在最上方
3. 点击一个船员（应该高亮或显示详情）
4. 点击"🏠 宿舍"按钮
5. 应该看到提示："XXX 已分配到 宿舍"
6. 该船员应该从待命列表消失，出现在宿舍列表

### 2. 测试重复分配保护
1. 选择一个已分配的船员（从宿舍/训练/任务部列表中）
2. 尝试再次点击分配按钮
3. 应该看到提示："XXX 已经分配到XXX，无法重复分配！"
4. 船员不应该移动

### 3. 测试未选择船员的提示
1. 不选择任何船员
2. 直接点击分配按钮
3. 应该看到提示："请先从待命列表中选择一名船员"

### 4. 测试每日重置
1. 为几个船员分配不同的地点
2. 点击"下一阶段"进入进展阶段
3. 再点击"下一阶段"回到调度阶段
4. 所有船员应该都回到"待命"列表
5. 天数应该+1

### 5. 测试效果生效
1. 分配船员到宿舍
2. 记录当前HP和能量
3. 进入进展阶段
4. 返回调度阶段
5. 检查HP和能量是否增加

---

## 📊 数值平衡

### 当前设置：
- **宿舍效果**：
  - HP +20
  - 能量 +25
  - 医生加成：额外HP +15
  - 痊愈条件：HP >= 60%最大HP

- **训练效果**：
  - XP +20
  - 能量 -10
  - 可能升级

- **任务指挥部**：
  - 无即时效果
  - 可以参加战斗
  - 战斗后有1天冷却

- **自然恢复**：
  - 所有船员能量 +5

---

## ⚠️ 注意事项

1. **存档兼容性**：旧存档中的船员分配状态会被保留，但下次进入进展阶段时会重置
2. **UI响应**：分配后立即更新UI，确保视觉反馈及时
3. **Toast提示**：使用不同长度的Toast（SHORT/LONG）区分重要程度
4. ** emoji使用**：增加了视觉吸引力，但在某些设备上可能显示不同

---

## 🚀 未来可能的改进

1. **分配历史**：显示船员昨天的去向
2. **批量分配**：一键分配所有船员到相同地点
3. **分配建议**：根据船员状态推荐最佳去向
4. **特殊事件**：某些天有特殊效果（如训练效果翻倍）
5. **成就系统**：连续X天将同一船员分配到同一地点

---

**最后更新**: 2026年4月18日  
**版本**: 2.1 - 每日分配系统优化
