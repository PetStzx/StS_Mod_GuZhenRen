package GuZhenRen.actions;

import GuZhenRen.powers.JianHenPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class FeiJianAction extends AbstractGameAction {
    private int dmg;

    public FeiJianAction(AbstractCreature target, int dmg) {
        this.target = target;
        this.dmg = dmg;
        this.actionType = ActionType.DAMAGE;
    }

    @Override
    public void update() {
        // 1. 如果当前目标已经死了，或者传进来是 null，自动找一个新的随机活怪
        if (this.target == null || this.target.isDeadOrEscaped()) {
            this.target = AbstractDungeon.getRandomMonster();
        }

        if (this.target != null && !this.target.isDeadOrEscaped()) {

            // 计算下一次弹跳的伤害（向下取整）
            int nextDmg = this.dmg / 2;

            // 步骤 3：如果伤害还能继续传，排入下一次的飞剑动作。
            if (nextDmg > 0) {
                this.addToTop(new FeiJianAction(null, nextDmg));
            }

            // 步骤 2：排入挂剑痕的动作
            this.addToTop(new ApplyPowerAction(this.target, AbstractDungeon.player, new JianHenPower(this.target, 1), 1));

            // 步骤 1：排入本次的伤害动作（因为是最后用 addToTop 添加的，所以最先执行）
            // 依然使用 NORMAL 伤害，这意味着这把飞剑的每一次弹跳都能触发你的《剑锋》效果！
            this.addToTop(new DamageAction(this.target,
                    new DamageInfo(AbstractDungeon.player, this.dmg, DamageInfo.DamageType.NORMAL),
                    AbstractGameAction.AttackEffect.SLASH_HORIZONTAL)); // 飞剑横斩特效
        }

        this.isDone = true;
    }
}