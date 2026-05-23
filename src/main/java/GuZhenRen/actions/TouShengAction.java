package GuZhenRen.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TouShengAction extends AbstractGameAction {
    private int stealAmount;

    public TouShengAction(int stealAmount) {
        this.actionType = ActionType.HEAL;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.stealAmount = stealAmount;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_XFAST) {
            int totalStolen = 0;

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    // 计算实际能偷到的上限值，不能超过怪物当前的最大生命值
                    int actualSteal = Math.min(this.stealAmount, mo.maxHealth);

                    if (actualSteal > 0) {
                        mo.maxHealth -= actualSteal;
                        totalStolen += actualSteal;

                        if (mo.currentHealth > mo.maxHealth) {
                            int hpLoss = mo.currentHealth - mo.maxHealth;
                            mo.currentHealth = mo.maxHealth;

                            DamageInfo fakeInfo = new DamageInfo(AbstractDungeon.player, hpLoss, DamageInfo.DamageType.HP_LOSS);
                            for (AbstractPower p : mo.powers) {
                                p.wasHPLost(fakeInfo, hpLoss);
                            }
                        }

                        // 如果最大生命值被偷光（<=0），直接死亡
                        if (mo.maxHealth <= 0) {
                            mo.currentHealth = 0;
                            mo.die();
                        }

                        mo.healthBarUpdatedEvent();
                    }
                }
            }

            if (totalStolen > 0) {
                AbstractDungeon.player.increaseMaxHp(totalStolen, true);
            }

            this.isDone = true;
        }
    }
}