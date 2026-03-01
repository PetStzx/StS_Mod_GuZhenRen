package GuZhenRen.actions;

import GuZhenRen.powers.JianHenPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class JianYingAction extends AbstractGameAction {
    private AbstractCard card; // 直接接收卡牌对象

    public JianYingAction(AbstractCard card) {
        this.card = card;
        this.actionType = ActionType.DAMAGE;
    }

    @Override
    public void update() {
        int maxStacks = -1;
        ArrayList<AbstractMonster> targets = new ArrayList<>();

        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped()) {
                int stacks = 0;
                if (m.hasPower(JianHenPower.POWER_ID)) {
                    stacks = m.getPower(JianHenPower.POWER_ID).amount;
                }

                if (stacks > maxStacks) {
                    maxStacks = stacks;
                    targets.clear();
                    targets.add(m);
                } else if (stacks == maxStacks) {
                    targets.add(m);
                }
            }
        }

        if (!targets.isEmpty()) {
            AbstractMonster target = targets.get(AbstractDungeon.cardRandomRng.random(targets.size() - 1));

            this.card.calculateCardDamage(target);

            // 使用计算后的最终伤害 this.card.damage
            DamageInfo info = new DamageInfo(AbstractDungeon.player, this.card.damage, DamageInfo.DamageType.NORMAL);

            this.addToTop(new DamageAction(target, info, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        }

        this.isDone = true;
    }
}