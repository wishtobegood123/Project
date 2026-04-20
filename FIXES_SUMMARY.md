# 游戏逻辑修复总结

## 🎯 修复的三个问题

### 问题 1: 船员可以无限休息和训练 ✅

**问题描述：**
- 船员在宿舍休息和训练时可以无限点击按钮
- 不会自动结束阶段
- 只有通过点击"下一阶段"才能进入下一阶段

**解决方案：**
添加了**行动次数限制系统**

#### 修改的文件：

1. **CrewMember.java** - 添加行动次数属性
   ```java
   private int actionsThisPhase; // 本阶段已执行的行动次数
   private static final int MAX_ACTIONS_PER_PHASE = 3; // 每阶段最多3次行动
   
   // 新增方法
   public int getActionsThisPhase() { return actionsThisPhase; }
   public void setActionsThisPhase(int actionsThisPhase) { this.actionsThisPhase = actionsThisPhase; }
   public void incrementActionsThisPhase() { this.actionsThisPhase++; }
   public boolean hasActionsRemaining() { return actionsThisPhase < MAX_ACTIONS_PER_PHASE; }
   public static int getMaxActionsPerPhase() { return MAX_ACTIONS_PER_PHASE; }
   public void resetActionsForNewPhase() { this.actionsThisPhase = 0; }
   ```

2. **CrewManager.java** - 训练和休息时消耗行动次数
   ```java
   public static void train(CrewMember crew) {
       if (crew == null) return;
       if (!crew.hasActionsRemaining()) return; // 检查行动次数
       crew.setXp(crew.getXp() + 25);
       crew.setEnergy(crew.getEnergy() - 15);
       crew.incrementActionsThisPhase(); // 消耗一次行动
       GameState.getInstance().getStatistics().incrementTrainingSessions();
       checkLevelUp(crew);
   }

   public static void rest(CrewMember crew) {
       if (crew == null) return;
       if (!crew.hasActionsRemaining()) return; // 检查行动次数
       crew.setHp(crew.getHp() + 25);
       crew.setEnergy(crew.getEnergy() + 35);
       crew.incrementActionsThisPhase(); // 消耗一次行动
       if (crew.isInjured() && crew.getHp() >= crew.getMaxHp() * 0.5f) {
           crew.setInjured(false);
       }
   }
   ```

3. **ProgressionManager.java** - 每天重置行动次数
   ```java
   for (CrewMember crew : crewList) {
       if (crew == null) continue;
       // 重置行动次数（新的一天）
       crew.resetActionsForNewPhase();
       // ... 其他处理
   }
   ```

4. **MainActivity.java** - UI显示行动次数并禁用按钮
   ```java
   // 显示行动次数
   String actionsText = " | 行动: " + selected.getActionsThisPhase() + "/" + CrewMember.getMaxActionsPerPhase();
   if (!selected.hasActionsRemaining()) {
       actionsText += " (本阶段无法继续行动)";
   }
   tvDetailProfession.setText(... + actionsText);
   
   // 根据行动次数启用/禁用按钮
   boolean hasActions = selected.hasActionsRemaining();
   btnTrain.setEnabled(hasActions);
   btnRest.setEnabled(hasActions);
   ```

**效果：**
- ✅ 每个船员每阶段（天）最多只能进行 3 次训练或休息
- ✅ UI 显示当前行动次数：`行动: 2/3`
- ✅ 当行动次数用完后，按钮自动禁用
- ✅ 进入下一阶段时，行动次数自动重置

---

### 问题 2: 敌人回合需要手动点击 ✅

**问题描述：**
- 玩家点击"结束回合"后，敌人回合确实会执行
- 但敌人回合结束后，UI 没有更新
- 控制按钮保持隐藏，玩家看不到如何继续操作

**根本原因：**
- `endPlayerTurn()` 调用后立即更新 UI（此时 `playerTurn = false`，按钮隐藏）
- 700ms 后敌人回合执行完毕，将 `playerTurn` 改回 `true`
- **但 UI 没有收到通知，所以按钮保持隐藏状态**

**解决方案：**
添加了**回合变化回调机制**

#### 修改的文件：

1. **CombatManager.java** - 添加回调接口
   ```java
   // 回合变化回调接口
   public interface OnTurnChangeListener {
       void onTurnChanged(CombatState state);
   }
   
   private static OnTurnChangeListener turnChangeListener;
   
   public static void setOnTurnChangeListener(OnTurnChangeListener listener) {
       turnChangeListener = listener;
   }
   ```

2. **CombatManager.java** - 敌人回合结束后触发回调
   ```java
   private static void performEnemyTurn(CombatState state) {
       // ... 敌人攻击逻辑
       
       checkCombatEnd(state);
       if (!state.isCombatEnded()) {
           state.setCurrentTurn(state.getCurrentTurn() + 1);
           state.setPlayerTurn(true);
           state.addLog(new CombatLogEntry("--- 第 " + state.getCurrentTurn() + " 回合 ---", CombatLogEntry.LogType.NORMAL));
           // ... 选择下一个角色
           
           // 通知 UI 更新（敌人回合结束，回到玩家回合）
           if (turnChangeListener != null) {
               final CombatState finalState = state;
               HANDLER.post(new Runnable() {
                   @Override
                   public void run() {
                       if (turnChangeListener != null) {
                           turnChangeListener.onTurnChanged(finalState);
                       }
                   }
               });
           }
       }
   }
   ```

3. **MainActivity.java** - 注册回调
   ```java
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       // ... 其他初始化
       
       // 注册战斗回合变化回调
       CombatManager.setOnTurnChangeListener(new CombatManager.OnTurnChangeListener() {
           @Override
           public void onTurnChanged(CombatState state) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       updateCombatUI();
                   }
               });
           }
       });
       
       updateUI();
   }
   ```

**效果：**
- ✅ 玩家点击"结束回合"后，控制按钮自动隐藏
- ✅ 敌人自动执行攻击（700ms 延迟）
- ✅ 敌人回合结束后，控制按钮自动重新显示
- ✅ 玩家可以看到"第 X 回合"的提示
- ✅ 不再需要手动点击任何按钮来继续玩家回合

---

### 问题 3: 敌人数量过少，难度过低 ✅

**问题描述：**
- 无论小队有多少人，敌人永远只有 2 个
- 敌人强度固定，不会随游戏进度变化
- 战斗缺乏挑战性

**解决方案：**
实现了**动态难度系统**

#### 修改的文件：

1. **CombatManager.java** - 动态生成敌人
   ```java
   public static CombatState startCombat() {
       // ... 初始化代码
       
       // 根据小队人数生成相应数量的敌人，增加难度
       int playerCount = state.getPlayerTeam().size();
       int enemyCount = Math.min(playerCount + 2, 5); // 敌人数量 = 玩家数 + 2，最多5个
       
       // 难度随天数增加
       int day = gameState.getDay();
       int levelBonus = day / 3; // 每3天敌人强化一级
       
       for (int i = 0; i < enemyCount; i++) {
           int enemyHp = 60 + (i * 20) + (levelBonus * 10);
           int enemyAttack = 12 + (i * 3) + (levelBonus * 2);
           int enemyDefense = 3 + (i * 2) + levelBonus;
           
           String enemyName;
           switch (i % 4) {
               case 0:
                   enemyName = "外星侦察兵";
                   break;
               case 1:
                   enemyName = "外星战士";
                   break;
               case 2:
                   enemyName = "外星精英";
                   enemyHp += 20;
                   enemyAttack += 5;
                   break;
               case 3:
                   enemyName = "外星巨兽";
                   enemyHp += 40;
                   enemyAttack += 3;
                   enemyDefense += 3;
                   break;
               default:
                   enemyName = "外星怪物";
           }
           
           state.getEnemyTeam().add(new Enemy(i + 1, enemyName, enemyHp, enemyAttack, enemyDefense));
       }
       
       state.addLog(new CombatLogEntry("战斗开始！遭遇 " + enemyCount + " 个敌人！", CombatLogEntry.LogType.NORMAL));
       // ... 其他代码
   }
   ```

**难度计算规则：**

| 因素 | 计算方式 | 效果 |
|------|---------|------|
| 敌人数量 | min(玩家数 + 2, 5) | 1人小队→3敌人，2人→4敌人，3人及以上→5敌人 |
| 基础属性 | 随敌人索引递增 | 第1个敌人最弱，第5个最强 |
| 天数加成 | 每3天提升一级 | 第3天开始敌人变强，第6天更强，以此类推 |
| 敌人类型 | 4种类型循环 | 侦察兵→战士→精英→巨兽→侦察兵... |

**四种敌人类型：**

1. **外星侦察兵**（基础型）
   - HP: 60 + 加成
   - 攻击: 12 + 加成
   - 防御: 3 + 加成

2. **外星战士**（中型）
   - HP: 80 + 加成
   - 攻击: 15 + 加成
   - 防御: 5 + 加成

3. **外星精英**（强力型）
   - HP: 100 + 加成（额外+20）
   - 攻击: 20 + 加成（额外+5）
   - 防御: 7 + 加成

4. **外星巨兽**（Boss型）
   - HP: 120 + 加成（额外+40）
   - 攻击: 18 + 加成（额外+3）
   - 防御: 9 + 加成（额外+3）

**效果：**
- ✅ 敌人数量与小队人数挂钩，更具挑战性
- ✅ 最多5个敌人，不会过于夸张
- ✅ 敌人强度随游戏天数逐步提升
- ✅ 4种不同类型的敌人，增加多样性
- ✅ 战斗日志显示敌人数量："战斗开始！遭遇 4 个敌人！"

---

## 📊 修改总结

### 修改的文件列表：

1. **CrewMember.java**
   - 添加行动次数属性和相关方法
   - 新增 6 个方法

2. **CrewManager.java**
   - train() 和 rest() 添加行动次数检查
   - 每次行动消耗一次行动次数

3. **ProgressionManager.java**
   - processProgression() 中添加行动次数重置

4. **CombatManager.java**
   - 添加回合变化回调接口
   - 动态生成敌人数量和强度
   - 敌人回合结束后触发 UI 更新

5. **MainActivity.java**
   - 注册战斗回合变化回调
   - UI 显示行动次数
   - 根据行动次数启用/禁用训练和休息按钮

### 技术亮点：

1. **回调机制** - 使用接口实现模块间通信，符合 SOLID 原则
2. **防御性编程** - 所有方法都添加了 null 检查
3. **游戏平衡** - 动态难度系统，随游戏进度递增
4. **用户体验** - UI 实时反馈，按钮自动启用/禁用

---

## 🎮 游戏体验改进

### 改进前：
- ❌ 可以无限训练/休息，没有策略性
- ❌ 敌人回合结束后需要手动操作
- ❌ 敌人永远只有2个，太简单

### 改进后：
- ✅ 每人每阶段3次行动，需要合理安排
- ✅ 敌人回合自动执行，流程顺畅
- ✅ 敌人数量随小队变化，难度递增
- ✅ UI 清晰显示行动次数和状态

---

## 🔧 测试建议

1. **测试行动次数限制：**
   - 选择一个船员
   - 连续点击"训练"或"休息"3次
   - 第4次应该无法点击（按钮变灰）
   - 查看显示："行动: 3/3 (本阶段无法继续行动)"

2. **测试敌人回合自动执行：**
   - 进入战斗
   - 点击"结束回合"
   - 等待 700ms，敌人应该自动攻击
   - 攻击完成后，控制按钮应该自动重新显示

3. **测试动态难度：**
   - 组建不同人数的小队（1-5人）
   - 开始战斗，查看敌人数量是否正确
   - 查看战斗日志："战斗开始！遭遇 X 个敌人！"
   - 进行多天后再次战斗，敌人应该更强

---

## 📝 符合课程要求

所有修改都使用了课程涵盖的内容：

- ✅ **变量、数据类型**：int, boolean, String
- ✅ **控制结构**：if, for, switch, while
- ✅ **数据结构**：ArrayList
- ✅ **面向对象**：封装（private 属性 + public 方法）
- ✅ **接口**：OnTurnChangeListener 回调接口
- ✅ **Android 基础**：runOnUiThread, Handler
- ✅ **防御性编程**：null 检查
- ✅ **错误处理**：条件判断

**没有使用任何超纲内容！**

---

*创建日期: 2026年4月18日*  
*修改内容: 修复三个游戏逻辑问题*  
*状态: 所有问题已解决*
