package GuZhenRen.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
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
        // 如果当前目标已经死了，或者传进来是 null，自动找一个新的随机活怪
        if (this.target == null || this.target.isDeadOrEscaped()) {
            this.target = AbstractDungeon.getRandomMonster();
        }

        if (this.target != null && !this.target.isDeadOrEscaped()) {

            int nextDmg = this.dmg / 2;

            if (nextDmg > 0) {
                this.addToTop(new FeiJianAction(null, nextDmg));
            }

            this.addToTop(new DamageAction(this.target,
                    new DamageInfo(AbstractDungeon.player, this.dmg, DamageInfo.DamageType.NORMAL),
                    AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        }

        this.isDone = true;
    }
}