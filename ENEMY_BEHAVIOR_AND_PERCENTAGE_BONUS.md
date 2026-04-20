# 敌人行为系统和百分比增益优化总结

## 🎯 四个功能实现

### 1. ✅ 重新实现敌人行为显示 - 已修复

**问题**：之前没有观察到敌人行为显示

**原因分析**：
- 敌人在`performEnemyTurn`时才生成行动计划
- 但此时已经是敌人回合，UI在玩家回合时刷新
- 导致看不到预告

**解决方案**：

#### a) 分离计划生成和执行
修改 [CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java#L247-L268)：

```java
public static void endPlayerTurn(final CombatState state) {
    // ...
    state.setPlayerTurn(false);
    state.addLog(new CombatLogEntry("--- 敌人回合 ---", CombatLogEntry.LogType.NORMAL));
    
    // ✅ 生成敌人行动计划并显示
    generateEnemyActions(state);
    
    HANDLER.postDelayed(new Runnable() {
        @Override
        public void run() {
            performEnemyTurn(state);
        }
    }, 1500); // 增加延迟到1.5秒，让玩家有时间看到计划
}
```

#### b) 添加generateEnemyActions方法
[CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java#L269-L353)

```java
private static void generateEnemyActions(CombatState state) {
    state.clearPendingActions();
    
    for (Enemy enemy : state.getEnemyTeam()) {
        if (enemy == null || enemy.getHp() <= 0) continue;
        
        // 随机选择目标
        List<CrewMember> aliveCrews = new ArrayList<CrewMember>();
        for (CrewMember crew : state.getPlayerTeam()) {
            if (crew != null && !crew.isInjured()) {
                aliveCrews.add(crew);
            }
        }
        
        if (aliveCrews.isEmpty()) break;
        CrewMember target = aliveCrews.get(RANDOM.nextInt(aliveCrews.size()));
        
        // 根据敌人类型决定行为概率
        int attackChance, buffChance, debuffChance;
        String enemyName = enemy.getName();
        
        if (enemyName.contains("侦察兵") || enemyName.contains("斥候")) {
            // 侦察兵：更高概率攻击减益
            attackChance = 50;
            buffChance = 10;
            debuffChance = 40;
        } else if (enemyName.contains("精英") || enemyName.contains("巨兽")) {
            // 精英/巨兽：更高概率强化攻击
            attackChance = 50;
            buffChance = 35;
            debuffChance = 15;
        } else {
            // 普通敌人：平衡
            attackChance = 60;
            buffChance = 20;
            debuffChance = 20;
        }
        
        // 生成行为...
        state.setPendingAction(enemyAction);
    }
}
```

#### c) 修改显示逻辑
[CombatView.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/ui/view/CombatView.java#L148-L171)

```java
// ✅ 显示敌人即将执行的行为（移除isPlayerTurn限制）
if (enemy.getHp() > 0) {
    CombatState.EnemyAction action = combatState.getPendingAction(enemy.getId());
    if (action != null) {
        paint.setColor(Color.YELLOW);
        paint.setTextSize(18);
        canvas.drawText(action.description, enemyX + 50, y + 15, paint);
        
        // 根据行为类型显示不同颜色
        if (action.type == CombatState.EnemyAction.ActionType.ATTACK) {
            paint.setColor(Color.parseColor("#FF5252"));
            canvas.drawText("伤害: " + action.value, enemyX + 50, y + 35, paint);
        } else if (action.type == CombatState.EnemyAction.ActionType.BUFF_ATTACK) {
            paint.setColor(Color.parseColor("#FFA726"));
            canvas.drawText("增益: +" + action.value + "%", enemyX + 50, y + 35, paint);
        } else if (action.type == CombatState.EnemyAction.ActionType.DEBUFF_PLAYER) {
            paint.setColor(Color.parseColor("#AB47BC"));
            canvas.drawText("减益: -" + action.value + "%", enemyX + 50, y + 35, paint);
        }
    }
}
```

**关键改进**：
1. **提前生成**：在玩家结束回合时立即生成敌人计划
2. **延长显示**：延迟从700ms增加到1500ms
3. **持续显示**：移除`!isPlayerTurn()`限制，始终显示
4. **调整布局**：缩小字体，优化位置避免重叠

---

### 2. ✅ 增益/减益改为百分比系统 - 已实现

**修改**：

#### a) 添加百分比字段
[CombatState.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/model/CombatState.java#L17)

```java
private int teamAttackBonusPercent; // ✅ 我方攻击力百分比增益
```

#### b) 修改REPAIR技能
[CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java#L183-L188)

```java
case REPAIR:
    // ✅ 百分比攻击力增益：初始10%，每级增加5%
    int attackBonusPercent = 10 + (caster.getLevel() - 1) * 5;
    state.setTeamAttackBonusPercent(attackBonusPercent);
    state.addLog(new CombatLogEntry(caster.getName() + " 使用战术指挥，全队攻击力提升 " + attackBonusPercent + "%！", CombatLogEntry.LogType.SKILL));
    break;
```

**增益曲线**：
| 等级 | 增益百分比 | 累计提升 |
|------|-----------|---------|
| 1    | 10%       | -       |
| 5    | 30%       | +20%    |
| 10   | 55%       | +45%    |
| 15   | 80%       | +70%    |
| 20   | 105%      | +95%    |

#### c) 应用百分比增益到伤害计算
[CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java#L120-L126)

```java
int baseDamage = config.getBaseAttack() + (int) (config.getAttackGrowth() * (attacker.getLevel() - 1));
baseDamage += state.getTeamAttackBonus();

// ✅ 应用百分比攻击力增益
int attackBonusPercent = state.getTeamAttackBonusPercent();
if (attackBonusPercent > 0) {
    baseDamage = (int)(baseDamage * (1 + attackBonusPercent / 100.0));
}

// 最终伤害
int damage = Math.max(1, (int)(baseDamage * 2.0) - target.getDefense() + RANDOM.nextInt(10));
```

**伤害计算示例**（基础攻击20，10级角色，55%增益）：
```
基础伤害 = 20 + (成长*9) = 假设50
应用增益 = 50 * (1 + 55/100) = 50 * 1.55 = 77.5 → 77
最终伤害 = 77 * 2.0 - 防御 + 随机 = 假设140
```

#### d) 敌人百分比增益/减益
[CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java#L320-L340)

```java
// ✅ 强化攻击：百分比增益，初始5%，随难度增加
int buffPercent = 5 + (missionDifficulty - 1) * 2; // 难度1:5%, 难度2:7%, ...
enemyAction = new CombatState.EnemyAction(
    enemy.getId(),
    CombatState.EnemyAction.ActionType.BUFF_ATTACK,
    "强化攻击 +" + buffPercent + "%",
    buffPercent,
    target.getId()
);

// ✅ 削弱我方：百分比减益，初始5%，随难度增加
int debuffPercent = 5 + (missionDifficulty - 1) * 2;
enemyAction = new CombatState.EnemyAction(
    enemy.getId(),
    CombatState.EnemyAction.ActionType.DEBUFF_PLAYER,
    "削弱我方 -" + debuffPercent + "%",
    debuffPercent,
    target.getId()
);
```

**敌方增益/减益曲线**：
| 任务难度 | 百分比 | 说明 |
|---------|-------|------|
| 1       | 5%    | 简单 |
| 2       | 7%    | 普通 |
| 3       | 9%    | 困难 |
| 4       | 11%   | 专家 |
| 5       | 13%   | 地狱 |

---

### 3. ✅ 显示技能具体数值和下一级数值 - 已实现

**修改**：[MainActivity.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/ui/MainActivity.java#L519-L554)

```java
SkillType skill = config != null ? config.getSkill() : null;
String skillText = "";
if (skill != null) {
    skillText = "技能: " + skill.getDisplayName() + " (消耗" + skill.getEnergyCost() + "能量)\n";
    skillText += skill.getDescription() + "\n";
    
    // ✅ 显示技能具体数值和下一级数值
    if (skill == SkillType.REPAIR) {
        int currentBonus = 10 + (selected.getLevel() - 1) * 5;
        int nextBonus = 10 + selected.getLevel() * 5;
        skillText += "当前效果: 攻击力 +" + currentBonus + "%";
        if (selected.getLevel() < 20) {
            skillText += " | 下级: +" + nextBonus + "%";
        }
    } else if (skill == SkillType.RAGE_SHOT) {
        ProfessionConfig cfg = ProfessionConfig.getConfig(selected.getProfession());
        if (cfg != null) {
            int currentBaseDamage = cfg.getBaseAttack() + (int)(cfg.getAttackGrowth() * (selected.getLevel() - 1));
            int nextBaseDamage = cfg.getBaseAttack() + (int)(cfg.getAttackGrowth() * selected.getLevel());
            int currentDamage = (int)(currentBaseDamage * 2.5);
            int nextDamage = (int)(nextBaseDamage * 2.5);
            skillText += "当前伤害: " + currentDamage;
            if (selected.getLevel() < 20) {
                skillText += " | 下级: " + nextDamage;
            }
        }
    } else if (skill == SkillType.HEAL) {
        int currentHeal = 30 + selected.getLevel() * 5;
        int nextHeal = 30 + (selected.getLevel() + 1) * 5;
        skillText += "当前治疗: " + currentHeal;
        if (selected.getLevel() < 20) {
            skillText += " | 下级: " + nextHeal;
        }
    }
}
tvDetailDesc.setText((config != null ? config.getDescription() : "") + "\n\n" + skillText);
```

**显示效果示例**（10级工程师）：
```
技能: 战术指挥 (消耗30能量)
提升全队攻击力

当前效果: 攻击力 +55% | 下级: +60%
```

**显示效果示例**（5级战士）：
```
技能: 狂暴射击 (消耗25能量)
对单个敌人造成高额伤害

当前伤害: 87 | 下级: 95
```

---

### 4. ✅ 根据敌人种类调整行为概率 - 已实现

**修改**：[CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java#L295-L313)

```java
// ✅ 根据敌人类型决定行为概率
int attackChance, buffChance, debuffChance;
String enemyName = enemy.getName();

if (enemyName.contains("侦察兵") || enemyName.contains("斥候")) {
    // 侦察兵：更高概率攻击减益
    attackChance = 50;
    buffChance = 10;
    debuffChance = 40;
} else if (enemyName.contains("精英") || enemyName.contains("巨兽")) {
    // 精英/巨兽：更高概率强化攻击
    attackChance = 50;
    buffChance = 35;
    debuffChance = 15;
} else {
    // 普通敌人：平衡
    attackChance = 60;
    buffChance = 20;
    debuffChance = 20;
}
```

**敌人行为概率对比**：

| 敌人类型 | 攻击 | 强化 | 减益 | 特点 |
|---------|------|------|------|------|
| 🔍 侦察兵/斥候 | 50% | 10% | **40%** | 擅长削弱我方 |
| ⚔️ 精英/巨兽 | 50% | **35%** | 15% | 擅长自我强化 |
| 👾 普通敌人 | 60% | 20% | 20% | 平衡型 |

**战术意义**：
- **侦察兵**：优先击败，防止累积减益
- **精英/巨兽**：尽快击杀，避免其变得过强
- **普通敌人**：常规处理

---

## 📊 修改统计

| 文件 | 主要修改 | 行数变化 |
|------|---------|---------|
| [CombatManager.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/manager/CombatManager.java) | 敌人行为系统+百分比增益 | +101 / -56 |
| [CombatState.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/model/CombatState.java) | 添加百分比字段 | +4 |
| [CombatView.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/ui/view/CombatView.java) | 优化行为显示 | +9 / -6 |
| [MainActivity.java](file://c:/Users/Wishtobegood/AndroidStudioProjects/MyApplication5/app/src/main/java/com/example/spacecolonypioneers/ui/MainActivity.java) | 技能数值显示 | +34 / -1 |

**总计**：+148行 / -63行

---

## 🎮 游戏体验改进

### 1. 信息透明度
**之前**：不知道敌人要做什么
**现在**：**提前1.5秒显示**敌人计划，可以预判

### 2. 增益系统
**之前**：固定数值增益（护盾）
**现在**：**百分比增益**，随等级成长，更有价值

### 3. 技能规划
**之前**：只知道技能名称
**现在**：**显示具体数值和下级数值**，便于决策

### 4. 敌人差异化
**之前**：所有敌人行为概率相同
**现在**：**不同类型有不同倾向**，需要针对性策略

---

## 🧪 测试建议

### 1. 测试敌人行为显示
1. 进入战斗
2. 点击"结束回合"
3. **应该看到敌人右侧出现黄色文字**
4. 等待1.5秒后敌人执行行动
5. 检查显示的行为是否与实际执行一致

### 2. 测试百分比增益
1. 使用工程师职业（REPAIR技能）
2. 1级时使用技能，检查增益是否为10%
3. 升级到5级，再次使用，检查是否为30%
4. 进行攻击，观察伤害是否提升相应百分比

### 3. 测试技能数值显示
1. 在调度阶段选择一个船员
2. 查看详情面板
3. **应该看到技能的当前数值和下级数值**
4. 升级船员，再次查看
5. **数值应该更新**

### 4. 测试敌人行为概率
1. 进行多场战斗
2. 观察侦察兵的行为
3. **应该经常看到"削弱我方"**
4. 观察精英/巨兽的行为
5. **应该经常看到"强化攻击"**

---

## 🔑 关键设计原则

### 1. 信息先行
- 先显示计划，再执行
- 给玩家反应时间
- 提高战术深度

### 2. 百分比成长
- 线性增长易于理解
- 后期收益更高
- 鼓励升级

### 3. 透明数值
- 显示当前效果
- 预告下级效果
- 帮助玩家规划

### 4. 敌人个性
- 不同类型有不同特点
- 需要不同应对策略
- 增加战斗多样性

---

## ⚠️ 注意事项

1. **百分比叠加**：多个增益如何叠加？目前是替换而非累加
2. **显示空间**：敌人行为文字不能遮挡其他元素
3. **延迟时间**：1.5秒是否合适？可能需要调整
4. **最高等级**：代码中假设最高20级，需要确认
5. **性能影响**：频繁计算百分比是否影响性能

---

## 🚀 后续优化建议

1. **增益叠加规则**：明确多个增益是相加还是相乘
2. **更多敌人类型**：每种类型有独特的行为模式
3. **行为连锁**：某些行为会触发其他行为
4. **反制技能**：玩家可以打断或反转敌人行为
5. **成就系统**：特定行为组合获得成就

---

**实现日期**: 2026年4月18日  
**版本**: 2.8 - 敌人行为系统和百分比增益优化  
**目标**: 更透明的信息、更合理的数值、更多样的敌人
