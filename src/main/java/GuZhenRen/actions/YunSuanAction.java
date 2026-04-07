package GuZhenRen.actions;

import GuZhenRen.util.IProbabilityCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class YunSuanAction extends AbstractGameAction {
    private float probIncrease;

    public YunSuanAction(float probIncrease) {
        this.probIncrease = probIncrease;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c instanceof IProbabilityCard) {
                ((IProbabilityCard) c).increaseBaseChance(this.probIncrease);
                c.superFlash();
            }
        }

        this.isDone = true;
    }
}