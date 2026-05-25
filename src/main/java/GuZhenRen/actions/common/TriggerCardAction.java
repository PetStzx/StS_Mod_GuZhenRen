package GuZhenRen.actions.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.List;

public class TriggerCardAction extends AbstractGameAction {
    private final AbstractCard card;
    private final AbstractMonster target;

    public TriggerCardAction(AbstractCard card, AbstractMonster target) {
        this.card = card;
        this.target = target;
        this.actionType = ActionType.USE;
    }

    @Override
    public void update() {
        AbstractCard originalCardInUse = AbstractDungeon.player.cardInUse;
        AbstractDungeon.player.cardInUse = this.card;

        int startIndex = AbstractDungeon.actionManager.actions.size();
        this.card.use(AbstractDungeon.player, this.target);
        int endIndex = AbstractDungeon.actionManager.actions.size();

        if (endIndex > startIndex) {
            List<AbstractGameAction> stolenActions = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {
                stolenActions.add(AbstractDungeon.actionManager.actions.get(i));
            }
            for (int i = 0; i < stolenActions.size(); i++) {
                AbstractDungeon.actionManager.actions.remove(AbstractDungeon.actionManager.actions.size() - 1);
            }

            AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractDungeon.player.cardInUse = originalCardInUse;
                    this.isDone = true;
                }
            });

            for (int i = stolenActions.size() - 1; i >= 0; i--) {
                AbstractDungeon.actionManager.addToTop(stolenActions.get(i));
            }
        } else {
            AbstractDungeon.player.cardInUse = originalCardInUse;
        }
        this.isDone = true;
    }
}