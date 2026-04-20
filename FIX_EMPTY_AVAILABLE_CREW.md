# 修复：任务选择阶段可用成员为空

## 🐛 问题描述

**现象**：进入任务选择阶段后，"可用成员"列表固定为空，无法选择任何船员加入小队。

**影响**：玩家无法组建小队，无法开始战斗。

---

## 🔍 问题根源

### 错误的重置时机

之前的代码在 **ProgressionManager.processProgression()** 结束时重置所有船员的分配状态：

```java
// ProgressionManager.java (错误的代码)
public static void processProgression() {
    // ... 处理宿舍、训练等效果
    
    // ❌ 错误：在进展阶段结束时重置
    for (CrewMember crew : crewList) {
        if (crew != null) {
            crew.setAssignment(Assignment.UNASSIGNED);
        }
    }
    
    state.setDay(state.getDay() + 1);
}
```

### 问题分析

游戏流程：
```
调度阶段 (SCHEDULING)
  ↓ 玩家分配船员到不同地点
进展阶段 (PROGRESSION)
  ↓ 应用效果（宿舍恢复、训练获得XP等）
  ↓ ❌ 重置所有船员为 UNASSIGNED
任务选择阶段 (MISSION_SELECTION)
  ↓ 过滤条件：只选择 MISSION_CONTROL 的船员
  ↓ ❌ 没有船员是 MISSION_CONTROL
  ↓ ❌ 可用成员列表为空！
```

**核心矛盾**：
- 任务选择阶段要求船员必须是 `MISSION_CONTROL` 状态
- 但进展阶段结束时所有船员都被重置为 `UNASSIGNED`
- 结果：没有任何船员符合条件

---

## ✅ 解决方案

### 正确的重置时机

将重置逻辑从 **进展阶段结束** 移到 **调度阶段开始**：

```
任务选择阶段 (MISSION_SELECTION)
  ↓ 玩家选择分配到 MISSION_CONTROL 的船员
  ↓ 开始战斗
战斗结束
  ↓ 返回任务选择阶段
  ↓ 点击"下一阶段"
调度阶段 (SCHEDULING)
  ↓ ✅ 重置所有船员为 UNASSIGNED
  ↓ 玩家可以重新分配
进展阶段 (PROGRESSION)
  ↓ 应用效果（基于当前分配）
  ↓ ✅ 不重置分配
任务选择阶段 (MISSION_SELECTION)
  ↓ ✅ 可以筛选出 MISSION_CONTROL 的船员
  ↓ ✅ 可用成员列表正常显示
```

---

## 📝 修改的文件

### 1. [ProgressionManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/ProgressionManager.java)

**删除**了重置分配的代码：

```diff
     public static void processProgression() {
         // ... 处理效果代码 ...
         
-        // 新的一天开始，重置所有船员的分配状态为待命
-        for (CrewMember crew : crewList) {
-            if (crew != null) {
-                crew.setAssignment(Assignment.UNASSIGNED);
-            }
-        }
-        
         state.setDay(state.getDay() + 1);
     }
```

---

### 2. [MainActivity.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/ui/MainActivity.java)

**添加**了在调度阶段开始时重置的逻辑：

```diff
     private void nextPhase() {
         Phase current = gameState.getCurrentPhase();
         if (current == null) current = Phase.SCHEDULING;
         switch (current) {
             case SCHEDULING:
                 gameState.setCurrentPhase(Phase.PROGRESSION);
                 ProgressionManager.processProgression();
                 gameState.setCurrentPhase(Phase.MISSION_SELECTION);
                 gameState.getCurrentSquad().clear();
                 viewFlipper.setDisplayedChild(1);
                 break;
             case PROGRESSION:
                 gameState.setCurrentPhase(Phase.MISSION_SELECTION);
                 viewFlipper.setDisplayedChild(1);
                 break;
             case MISSION_SELECTION:
                 gameState.setCurrentPhase(Phase.SCHEDULING);
+                // ✅ 新的一天开始，重置所有船员的分配状态为待命
+                for (CrewMember crew : gameState.getCrewList()) {
+                    if (crew != null) {
+                        crew.setAssignment(Assignment.UNASSIGNED);
+                    }
+                }
                 viewFlipper.setDisplayedChild(0);
                 break;
             default:
                 break;
         }
         updateUI();
     }
```

---

## 🎮 新的游戏流程

### 完整的每日循环

```
┌─────────────────────────────────────┐
│   第 N 天 - 调度阶段 (SCHEDULING)   │
├─────────────────────────────────────┤
│ ✅ 所有船员重置为"待命"状态          │
│                                     │
│ 1. 查看待命船员列表                  │
│ 2. 选择船员 A → 分配到"宿舍"        │
│ 3. 选择船员 B → 分配到"训练"        │
│ 4. 选择船员 C → 分配到"任务指挥部"  │
│ 5. 点击"下一阶段"                   │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│   第 N 天 - 进展阶段 (PROGRESSION)  │
├─────────────────────────────────────┤
│ ✅ 保持分配状态不变                  │
│                                     │
│ 自动处理：                          │
│ - 船员 A (宿舍): HP+20, 能量+25     │
│ - 船员 B (训练): XP+20, 能量-10     │
│ - 船员 C (任务部): 无即时效果       │
│ - 所有船员: 战斗冷却-1, 能量+5      │
│                                     │
│ 点击"下一阶段"                       │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│ 第 N 天 - 任务选择 (MISSION_SELECT) │
├─────────────────────────────────────┤
│ ✅ 保持分配状态不变                  │
│                                     │
│ 1. 选择任务                         │
│ 2. 查看"可用成员"列表               │
│    → 只显示船员 C (MISSION_CONTROL) │
│ 3. 将船员 C 加入小队                │
│ 4. 点击"开始任务"                   │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│       战斗阶段 (COMBAT)             │
├─────────────────────────────────────┤
│ 回合制战斗                           │
│ 战斗结束后标记参战船员              │
│ 需要休息1天才能再次参战             │
│                                     │
│ 战斗胜利/失败                        │
│ 返回任务选择阶段                     │
└──────────────┬──────────────────────┘
               ↓
         点击"下一阶段"
               ↓
┌─────────────────────────────────────┐
│  第 N+1 天 - 调度阶段 (SCHEDULING)  │
├─────────────────────────────────────┤
│ ✅ 所有船员重置为"待命"状态          │
│ ✅ 开始新的循环                      │
└─────────────────────────────────────┘
```

---

## 🧪 测试步骤

### 1. 测试分配和重置
1. 启动游戏，进入调度阶段
2. 确认所有船员都在"待命"列表
3. 分配几个船员到不同地点
4. 点击"下一阶段"进入进展阶段
5. 再点击"下一阶段"进入任务选择阶段
6. **应该能看到分配到任务指挥部的船员在"可用成员"列表中**
7. 继续点击"下一阶段"回到调度阶段
8. **所有船员应该都回到"待命"列表**

### 2. 测试任务选择
1. 在调度阶段，分配至少一个船员到"任务指挥部"
2. 进入任务选择阶段
3. 查看"可用成员"列表
4. **应该能看到分配到任务指挥部的船员**
5. 点击船员加入小队
6. 点击"开始任务"进入战斗

### 3. 测试战斗后可用成员
1. 完成一场战斗
2. 返回任务选择阶段
3. 查看"可用成员"列表
4. **参战的船员应该不在列表中**（因为需要休息1天）
5. 其他分配到任务指挥部的船员应该仍然可见

---

## 🔑 关键设计原则

### 1. 分配状态的持久性
- **分配一旦设定，持续到下次重置**
- 不会因为阶段切换而丢失
- 确保任务选择时能正确筛选

### 2. 重置时机的合理性
- **在每天开始时重置**（调度阶段）
- 符合"每天重新规划"的游戏理念
- 给玩家清晰的起点

### 3. 状态转换的清晰性
```
调度阶段 → 进展阶段：保持分配
进展阶段 → 任务选择：保持分配
任务选择 → 调度阶段：✅ 重置分配
```

---

## ⚠️ 注意事项

1. **不要在其他地方重置分配**：只在调度阶段开始时重置
2. **战斗冷却独立于分配**：即使重置分配，战斗冷却仍然有效
3. **受伤状态独立于分配**：受伤船员即使重置分配仍然是受伤的
4. **小队清空时机**：每次进入任务选择阶段时清空小队（已在代码中实现）

---

## 📊 修改统计

| 文件 | 修改类型 | 行数变化 |
|------|---------|---------|
| [ProgressionManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/ProgressionManager.java) | 删除重置逻辑 | -7 |
| [MainActivity.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/ui/MainActivity.java) | 添加重置逻辑 | +6 |

**总计**：+6行 / -7行

---

## 🚀 后续优化建议

1. **视觉反馈**：在重置分配时显示提示："新的一天开始了！请重新分配船员。"
2. **分配记忆**：显示昨天的分配情况，帮助玩家决策
3. **快速分配**：提供"重复昨天分配"的快捷按钮
4. **分配建议**：根据船员状态推荐最佳分配方案

---

**修复日期**: 2026年4月18日  
**问题编号**: FIX-003  
**严重程度**: 🔴 高（阻止游戏进行）  
**修复状态**: ✅ 已完成
