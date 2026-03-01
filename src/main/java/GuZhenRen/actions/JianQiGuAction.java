package GuZhenRen.actions;

import GuZhenRen.powers.JianHenPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class JianQiGuAction extends AbstractGameAction {
    private DamageInfo info;
    private int multiplier;

    public JianQiGuAction(AbstractCreature target, DamageInfo info, int multiplier) {
        this.target = target;
        this.info = info;
        this.multiplier = multiplier;
        this.actionType = ActionType.DAMAGE;
    }

    @Override
    public void update() {
        // 1. 读取目标当前的剑痕层数
        int stacks = 0;
        if (this.target != null && this.target.hasPower(JianHenPower.POWER_ID)) {
            stacks = this.target.getPower(JianHenPower.POWER_ID).amount;
        }

        // 2. 先把群体伤害排入队列 (后进先出，所以它会被压在单体伤害后面执行)
        if (stacks > 0) {
            int aoeDamage = stacks * this.multiplier;

            // 构造固定数值的伤害数组
            int[] damageArray = new int[AbstractDungeon.getCurrRoom().monsters.monsters.size()];
            for (int i = 0; i < damageArray.length; i++) {
                damageArray[i] = aoeDamage;
            }

            // 排入群体伤害动作
            this.addToTop(new DamageAllEnemiesAction(
                    AbstractDungeon.player,
                    damageArray,
                    DamageInfo.DamageType.NORMAL,
                    AbstractGameAction.AttackEffect.SLASH_DIAGONAL
            ));
        }

        // 3. 将第一段单体伤害排入顶部 (最先执行)
        this.addToTop(new DamageAction(
                this.target,
                this.info,
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL
        ));

        this.isDone = true;
    }
}