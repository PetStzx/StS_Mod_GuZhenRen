package GuZhenRen.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TouShengAction extends AbstractGameAction {
    private int stealAmount;

    public TouShengAction(int stealAmount) {
        this.actionType = ActionType.HEAL; // 治疗类型动作
        this.duration = Settings.ACTION_DUR_XFAST;
        this.stealAmount = stealAmount;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_XFAST) {
            int totalStolen = 0;

            // 遍历所有怪物
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    // 特殊情况2：计算实际能偷到的上限值（不能超过怪物当前的最大生命值）
                    int actualSteal = Math.min(this.stealAmount, mo.maxHealth);

                    if (actualSteal > 0) {
                        // 扣除怪物最大生命值
                        mo.maxHealth -= actualSteal;
                        totalStolen += actualSteal;

                        // 如果怪物当前血量超出了新的最大生命值，压低当前血量
                        if (mo.currentHealth > mo.maxHealth) {
                            mo.currentHealth = mo.maxHealth;
                        }

                        // 特殊情况1：如果最大生命值被偷光（<=0），直接死亡
                        if (mo.maxHealth <= 0) {
                            mo.currentHealth = 0;
                            mo.die();
                        }

                        // 刷新怪物的血条 UI 显示
                        mo.healthBarUpdatedEvent();
                    }
                }
            }

            // 如果成功偷到了寿命，统一加给玩家
            if (totalStolen > 0) {
                AbstractDungeon.player.increaseMaxHp(totalStolen, true);
            }

            this.isDone = true;
        }
    }
}