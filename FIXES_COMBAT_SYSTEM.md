# 战斗系统修复总结

## 🎯 修复的三个问题

### 1. ✅ 战斗前只能选择分配到任务指挥部的船员
**问题**：之前可以去宿舍或训练的船员也能参加战斗，分配机制没有生效

**解决方案**：
- 在 `updateMissionUI()` 中添加过滤条件
- 只显示 `Assignment.MISSION_CONTROL` 的船员
- 确保只有分配到任务指挥部的船员可以加入小队

**修改文件**：[MainActivity.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/ui/MainActivity.java#L419-L441)

```java
// 只显示分配到任务指挥部、未受伤且冷却完成的船员
if (crew != null && 
    crew.getAssignment() == Assignment.MISSION_CONTROL &&  // ✅ 必须分配到任务指挥部
    !crew.isInjured() && 
    crew.isAvailableForCombat() && 
    !gameState.getCurrentSquad().contains(crew)) {
    available.add(crew);
}
```

---

### 2. ✅ 每个船员每回合只能行动一次
**问题**：一个回合内，我方船员可以不点击"结束回合"而无限行动

**解决方案**：
- 在 `CombatState` 中添加 `actedCrewIds` 列表跟踪已行动的船员
- 在 `performAttack()` 和 `useSkill()` 中检查船员是否已行动
- 行动后标记船员，防止重复行动
- 结束回合时重置所有标记

**修改文件**：

#### a) [CombatState.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/model/CombatState.java)
```java
private List<Integer> actedCrewIds; // 本回合已经行动过的船员ID

// 新增方法
public List<Integer> getActedCrewIds() { return actedCrewIds; }
public void resetActedCrews() { actedCrewIds.clear(); }
public boolean hasCrewActed(int crewId) { return actedCrewIds.contains(crewId); }
public void markCrewAsActed(int crewId) { 
    if (!actedCrewIds.contains(crewId)) {
        actedCrewIds.add(crewId);
    }
}
```

#### b) [CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java) - performAttack()
```java
public static void performAttack(CombatState state) {
    // ... 前置检查
    
    // ✅ 检查船员是否已经行动过
    if (state.hasCrewActed(attacker.getId())) {
        state.addLog(new CombatLogEntry(attacker.getName() + " 本回合已经行动过了！", CombatLogEntry.LogType.NORMAL));
        return;
    }
    
    // ... 执行攻击
    
    // ✅ 标记船员已行动
    state.markCrewAsActed(attacker.getId());
}
```

#### c) [CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java) - useSkill()
```java
public static void useSkill(CombatState state) {
    // ... 前置检查
    
    // ✅ 检查船员是否已经行动过
    if (state.hasCrewActed(caster.getId())) {
        state.addLog(new CombatLogEntry(caster.getName() + " 本回合已经行动过了！", CombatLogEntry.LogType.NORMAL));
        return;
    }
    
    // ... 执行技能
    
    // ✅ 标记船员已行动
    state.markCrewAsActed(caster.getId());
}
```

#### d) [CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java) - endPlayerTurn()
```java
public static void endPlayerTurn(final CombatState state) {
    if (state == null || state.isCombatEnded()) return;
    
    // ✅ 重置所有船员的行动标记（新回合开始）
    state.resetActedCrews();
    
    state.setPlayerTurn(false);
    // ...
}
```

#### e) [CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java) - startCombat()
```java
public static CombatState startCombat() {
    // ... 初始化代码
    
    // ✅ 重置行动标记
    state.resetActedCrews();
    
    state.setSelectedCrew(state.getPlayerTeam().get(0));
    // ...
}
```

---

### 3. ✅ 敌人随机攻击不同目标
**问题**：所有敌人都攻击同一个船员（第一个未受伤的船员）

**解决方案**：
- 收集所有存活的船员到列表
- 使用 `RANDOM.nextInt()` 随机选择一个目标
- 每个敌人独立随机选择目标

**修改文件**：[CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java#L250-L281)

```java
private static void performEnemyTurn(CombatState state) {
    for (Enemy enemy : state.getEnemyTeam()) {
        if (enemy == null || enemy.getHp() <= 0) continue;
        
        // ✅ 随机选择一个未受伤的船员作为目标
        List<CrewMember> aliveCrews = new ArrayList<CrewMember>();
        for (CrewMember crew : state.getPlayerTeam()) {
            if (crew != null && !crew.isInjured()) {
                aliveCrews.add(crew);
            }
        }
        
        if (aliveCrews.isEmpty()) break;
        
        // 随机选择目标
        CrewMember target = aliveCrews.get(RANDOM.nextInt(aliveCrews.size()));
        
        // ... 执行攻击
    }
}
```

---

## 📊 修改的文件清单

| 文件 | 修改内容 | 行数变化 |
|------|---------|---------|
| [MainActivity.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/ui/MainActivity.java) | 添加任务指挥部过滤条件 | +6 / -2 |
| [CombatState.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/model/CombatState.java) | 添加行动跟踪字段和方法 | +12 |
| [CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java) | 1. 添加行动检查<br>2. 添加行动标记<br>3. 重置行动标记<br>4. 敌人随机攻击 | +31 / -6 |

**总计**：+49行 / -8行

---

## 🎮 新的战斗流程

### 玩家回合
```
第1步：选择船员A
   ↓
第2步：点击"攻击"或"技能"
   ↓
✅ 检查：船员A是否已行动？
   - 是 → 提示"本回合已经行动过了！"
   - 否 → 执行行动，标记为已行动
   ↓
第3步：自动切换到下一个船员
   ↓
第4步：选择船员B
   ↓
第5步：点击"攻击"或"技能"
   ↓
✅ 检查：船员B是否已行动？
   - 是 → 提示"本回合已经行动过了！"
   - 否 → 执行行动，标记为已行动
   ↓
... 所有船员都行动过或手动点击"结束回合"
   ↓
进入敌人回合
```

### 敌人回合
```
敌人1：随机选择目标 → 攻击
   ↓
敌人2：随机选择目标（可能与敌人1相同或不同）→ 攻击
   ↓
敌人3：随机选择目标 → 攻击
   ↓
... 所有敌人攻击完毕
   ↓
重置所有船员的行动标记
   ↓
进入下一回合（玩家回合）
```

---

## 🧪 测试建议

### 1. 测试任务指挥部分配
1. 在调度阶段，将一些船员分配到宿舍，一些到训练，一些到任务指挥部
2. 进入任务选择阶段
3. 查看"可用成员"列表
4. **应该只显示分配到任务指挥部的船员**
5. 尝试将宿舍或训练的船员加入小队
6. **应该无法看到这些船员**

### 2. 测试船员行动限制
1. 进入战斗
2. 选择第一个船员，点击"攻击"
3. 再次选择同一个船员，点击"攻击"
4. **应该看到提示："XXX 本回合已经行动过了！"**
5. 点击"技能"按钮
6. **应该看到同样的提示**
7. 点击"结束回合"
8. 等待敌人回合结束
9. 再次选择刚才的船员
10. **现在应该可以正常行动了**

### 3. 测试敌人随机攻击
1. 组建一个3人以上的小队
2. 进入战斗
3. 观察战斗日志
4. **应该看到不同的敌人攻击不同的船员**
5. 多次战斗，确认攻击目标是随机的
6. **不应该出现所有敌人都攻击同一个人的情况**

---

## 🔍 关键设计决策

### 1. 为什么使用ID而不是对象引用跟踪行动？
- **序列化友好**：ID是整数，易于保存和加载
- **避免引用问题**：不会因为对象重建而失效
- **性能更好**：整数比较比对象比较快

### 2. 为什么在结束回合时重置标记？
- **回合制逻辑**：每个回合都是独立的
- **公平性**：所有船员在新回合都有平等的行动机会
- **简化逻辑**：不需要复杂的清理逻辑

### 3. 为什么敌人要随机攻击？
- **游戏平衡**：防止某个船员被集火秒杀
- **策略深度**：玩家需要考虑整体队伍的生存
- **真实感**：模拟混乱的战场环境

### 4. 为什么允许手动结束回合？
- **战术选择**：可能想保留某些船员的行动
- **风险控制**：不想让所有船员都暴露
- **玩家控制**：给予玩家更多决策权

---

## ⚠️ 注意事项

1. **行动标记只在战斗中有效**：不会影响调度阶段的分配
2. **重置时机很重要**：必须在正确的时机重置，否则会导致bug
3. **随机数的种子**：使用固定的Random实例，保证可重复性
4. **空列表检查**：随机选择前必须检查列表是否为空

---

## 🚀 未来可能的改进

1. **行动顺序显示**：在UI中显示哪些船员已经行动过
2. **强制结束回合**：当所有船员都行动过后自动结束回合
3. **敌人AI优化**：根据船员血量、职业等因素智能选择目标
4. **行动点数系统**：不同行动消耗不同点数
5. **连击系统**：特定条件下可以再次行动

---

**最后更新**: 2026年4月18日  
**版本**: 2.2 - 战斗系统修复
