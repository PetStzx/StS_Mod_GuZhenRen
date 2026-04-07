package GuZhenRen.actions;

import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ShanJianGuAction extends AbstractGameAction {

    public ShanJianGuAction() {
        // 不需要设置太长的持续时间，瞬间结算即可
        this.duration = 0.0F;
    }

    @Override
    public void update() {
        for (AbstractCard c : DrawCardAction.drawnCards) {
            if (!c.hasTag(GuZhenRenTags.JIAN_DAO)) {
                AbstractDungeon.player.hand.moveToDiscardPile(c);
                c.triggerOnManualDiscard();
                com.megacrit.cardcrawl.core.GameCursor.hidden = false;
            }
        }

        DrawCardAction.drawnCards.clear();

        this.isDone = true;
    }
}