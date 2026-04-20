package com.example.spacecolonypioneers.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.spacecolonypioneers.model.CombatState;
import com.example.spacecolonypioneers.model.CrewMember;
import com.example.spacecolonypioneers.model.Enemy;

public class CombatView extends View {
    private CombatState combatState;
    private Paint paint;
    private float spScale;

    public CombatView(Context context) {
        super(context);
        init();
    }

    public CombatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        spScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        paint.setTextSize(20 * spScale);
    }

    public void setCombatState(CombatState combatState) {
        this.combatState = combatState;
        invalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && combatState != null && combatState.isPlayerTurn()) {
            float x = event.getX();
            float y = event.getY();
            
            float enemyX = getWidth() * 0.75f;
            float startY = 120;
            float spacing = 160;
            int enemyIndex = 0;
            for (Enemy enemy : combatState.getEnemyTeam()) {
                if (enemy != null && enemy.getHp() > 0) {
                    float enemyY = startY + enemyIndex * spacing;
                    float distance = (float) Math.sqrt(Math.pow(x - enemyX, 2) + Math.pow(y - enemyY, 2));
                                
                    if (distance < 45) {
                        combatState.setSelectedEnemy(enemy);
                        invalidate();
                        return true;
                    }
                }
                enemyIndex++;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (combatState == null) return;

        int width = getWidth();
        int height = getHeight();
        paint.setColor(Color.parseColor("#0d1b2a"));
        canvas.drawRect(0, 0, width, height, paint);

        float playerX = width * 0.25f;
        float enemyX = width * 0.75f;
        float startY = 50;
        float bottomInfoHeight = height * 0.15f;
        float availableHeight = height - bottomInfoHeight - startY - 30;
        int maxCount = Math.max(combatState.getPlayerTeam().size(), combatState.getEnemyTeam().size());
        float spacing = Math.max(110f, Math.min(160f, availableHeight / maxCount));
        float nameOffset = 35f;
        float hpOffset = 75f;
        float shieldOffset = 115f;
        float energyOffset = 155f;
        float lastUnitY = startY + (maxCount - 1) * spacing + energyOffset;

        int playerIndex = 0;
        for (CrewMember crew : combatState.getPlayerTeam()) {
            if (crew == null) continue;
            float y = startY + playerIndex * spacing;
            if (crew == combatState.getSelectedCrew() && combatState.isPlayerTurn()) {
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(playerX - 50, y, 14, paint);
            }
            if (crew.getShield() > 0) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                paint.setColor(Color.parseColor("#4fc3f7"));
                canvas.drawCircle(playerX, y, 44, paint);
                paint.setStyle(Paint.Style.FILL);
            }
            paint.setColor(crew.isInjured() ? Color.GRAY : Color.parseColor("#43a047"));
            canvas.drawCircle(playerX, y, 40, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(16 * spScale);
            canvas.drawText(crew.getName(), playerX - 38, y + nameOffset, paint);
            canvas.drawText(crew.getHp() + "/" + crew.getMaxHp(), playerX - 38, y + hpOffset, paint);
            if (crew.getShield() > 0) {
                canvas.drawText("SH " + crew.getShield(), playerX - 38, y + shieldOffset, paint);
            }
            paint.setColor(Color.parseColor("#2196F3"));
            paint.setTextSize(14 * spScale);
            canvas.drawText("EN " + crew.getEnergy() + "/" + crew.getMaxEnergy(), playerX - 38, crew.getShield() > 0 ? y + energyOffset : y + shieldOffset, paint);
            playerIndex++;
        }

        int enemyIndex = 0;
        for (Enemy enemy : combatState.getEnemyTeam()) {
            if (enemy == null) continue;
            float y = startY + enemyIndex * spacing;
            if (enemy == combatState.getSelectedEnemy()) {
                paint.setColor(Color.RED);
                canvas.drawCircle(enemyX + 50, y, 14, paint);
            }
            paint.setColor(enemy.getHp() <= 0 ? Color.GRAY : Color.parseColor("#e53935"));
            canvas.drawCircle(enemyX, y, 40, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(16 * spScale);
            canvas.drawText(enemy.getName(), enemyX - 54, y + nameOffset, paint);
            canvas.drawText(Math.max(0, enemy.getHp()) + "/" + enemy.getMaxHp(), enemyX - 54, y + hpOffset, paint);
            if (enemy.getHp() > 0) {
                CombatState.EnemyAction action = combatState.getPendingAction(enemy.getId());
                if (action != null) {
                    paint.setColor(Color.YELLOW);
                    paint.setTextSize(12 * spScale);
                    canvas.drawText(action.description, enemyX + 60, y + 14, paint);
                    if (action.type == CombatState.EnemyAction.ActionType.ATTACK) {
                        paint.setColor(Color.parseColor("#FF5252"));
                        paint.setTextSize(11 * spScale);
                        canvas.drawText("Damage: " + action.value, enemyX + 60, y + 36, paint);
                    } else if (action.type == CombatState.EnemyAction.ActionType.BUFF_ATTACK) {
                        paint.setColor(Color.parseColor("#FFA726"));
                        paint.setTextSize(11 * spScale);
                        canvas.drawText("Buff: +" + action.value + "%", enemyX + 60, y + 36, paint);
                    } else if (action.type == CombatState.EnemyAction.ActionType.DEBUFF_PLAYER) {
                        paint.setColor(Color.parseColor("#AB47BC"));
                        paint.setTextSize(11 * spScale);
                        canvas.drawText("Debuff: -" + action.value + "%", enemyX + 60, y + 36, paint);
                    }
                }
            }
            
            enemyIndex++;
        }

        paint.setColor(Color.WHITE);
        paint.setTextSize(16 * spScale);
        String turnText = combatState.isPlayerTurn() ? "Your Turn" : "Enemy Turn";
        canvas.drawText(turnText, width / 2f - 50, 40, paint);
        if (combatState.isPlayerTurn()) {
            float infoStartY = Math.min(height - bottomInfoHeight + 10, lastUnitY + 20);
            float infoHeight = 12 * spScale;
            float progressWidth = width * 0.25f;
            CrewMember selectedCrew = combatState.getSelectedCrew();
            if (selectedCrew != null) {
                float lineY = infoStartY;
                paint.setColor(Color.parseColor("#43a047"));
                paint.setTextSize(14 * spScale);
                canvas.drawText("🔹 " + selectedCrew.getName(), 20, lineY + 18, paint);
                float hpBarX = 20;
                float hpBarY = lineY + 14 * spScale;
                paint.setColor(Color.parseColor("#424242"));
                canvas.drawRect(hpBarX, hpBarY, hpBarX + progressWidth, hpBarY + infoHeight, paint);
                float hpPercent = (float) selectedCrew.getHp() / selectedCrew.getMaxHp();
                paint.setColor(Color.parseColor("#4CAF50"));
                canvas.drawRect(hpBarX, hpBarY, hpBarX + progressWidth * hpPercent, hpBarY + infoHeight, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(12 * spScale);
                canvas.drawText("HP: " + selectedCrew.getHp() + "/" + selectedCrew.getMaxHp(), hpBarX + progressWidth + 10, hpBarY + 14, paint);
                float energyBarY = lineY + 32 * spScale;
                paint.setColor(Color.parseColor("#424242"));
                canvas.drawRect(hpBarX, energyBarY, hpBarX + progressWidth, energyBarY + infoHeight, paint);
                float energyPercent = (float) selectedCrew.getEnergy() / selectedCrew.getMaxEnergy();
                paint.setColor(Color.parseColor("#2196F3"));
                canvas.drawRect(hpBarX, energyBarY, hpBarX + progressWidth * energyPercent, energyBarY + infoHeight, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(12 * spScale);
                canvas.drawText("Energy: " + selectedCrew.getEnergy() + "/" + selectedCrew.getMaxEnergy(), hpBarX + progressWidth + 10, energyBarY + 14, paint);
                if (selectedCrew.getShield() > 0) {
                    float shieldBarY = lineY + 50 * spScale;
                    paint.setColor(Color.parseColor("#424242"));
                    canvas.drawRect(hpBarX, shieldBarY, hpBarX + progressWidth, shieldBarY + infoHeight, paint);
                    float shieldPercent = (float) selectedCrew.getShield() / selectedCrew.getMaxShield();
                    paint.setColor(Color.parseColor("#4FC3F7"));
                    canvas.drawRect(hpBarX, shieldBarY, hpBarX + progressWidth * shieldPercent, shieldBarY + infoHeight, paint);
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(12 * spScale);
                    canvas.drawText("Shield: " + selectedCrew.getShield(), hpBarX + progressWidth + 10, shieldBarY + 14, paint);
                }
            }
            Enemy selectedEnemy = combatState.getSelectedEnemy();
            if (selectedEnemy != null && selectedEnemy.getHp() > 0) {
                float lineY = infoStartY + (selectedCrew != null && selectedCrew.getShield() > 0 ? 68 * spScale : 50 * spScale);
                paint.setColor(Color.parseColor("#e53935"));
                paint.setTextSize(14 * spScale);
                canvas.drawText("🔸 " + selectedEnemy.getName(), 20, lineY + 18, paint);
                float hpBarX = 20;
                float hpBarY = lineY + 14 * spScale;
                paint.setColor(Color.parseColor("#424242"));
                canvas.drawRect(hpBarX, hpBarY, hpBarX + progressWidth, hpBarY + infoHeight, paint);
                float hpPercent = (float) selectedEnemy.getHp() / selectedEnemy.getMaxHp();
                paint.setColor(Color.parseColor("#F44336"));
                canvas.drawRect(hpBarX, hpBarY, hpBarX + progressWidth * hpPercent, hpBarY + infoHeight, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(12 * spScale);
                canvas.drawText("HP: " + selectedEnemy.getHp() + "/" + selectedEnemy.getMaxHp(), hpBarX + progressWidth + 10, hpBarY + 14, paint);
                CombatState.EnemyAction action = combatState.getPendingAction(selectedEnemy.getId());
                if (action != null) {
                    float actionY = lineY + 32 * spScale;
                    paint.setColor(Color.YELLOW);
                    paint.setTextSize(12 * spScale);
                    if (action.type == CombatState.EnemyAction.ActionType.ATTACK) {
                        paint.setColor(Color.parseColor("#FF5252"));
                        canvas.drawText("⚡ " + action.description + " (Damage: " + action.value + ")", 20, actionY + 20, paint);
                    } else if (action.type == CombatState.EnemyAction.ActionType.BUFF_ATTACK) {
                        paint.setColor(Color.parseColor("#FFA726"));
                        canvas.drawText("⚡ " + action.description + " (+" + action.value + "%)", 20, actionY + 20, paint);
                    } else if (action.type == CombatState.EnemyAction.ActionType.DEBUFF_PLAYER) {
                        paint.setColor(Color.parseColor("#AB47BC"));
                        canvas.drawText("⚡ " + action.description + " (-" + action.value + "%)", 20, actionY + 20, paint);
                    }
                }
            }
        }
    }
}
