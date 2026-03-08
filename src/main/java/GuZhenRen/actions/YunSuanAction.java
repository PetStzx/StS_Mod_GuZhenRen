package GuZhenRen.actions;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.IProbabilityCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;

public class YunSuanAction extends AbstractGameAction {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(GuZhenRen.makeID("YunSuan"));

    private AbstractPlayer p;
    private int drawAmount;
    private float probIncrease;

    public YunSuanAction(int drawAmount, float probIncrease) {
        this.p = AbstractDungeon.player;
        this.drawAmount = drawAmount;
        this.probIncrease = probIncrease;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (p.hand.isEmpty()) {
                addToTop(new BuffProbabilityAction(probIncrease));
                addToTop(new DrawCardAction(p, drawAmount));
                this.isDone = true;
                return;
            }

            String msg = cardStrings.EXTENDED_DESCRIPTION[0];
            AbstractDungeon.handCardSelectScreen.open(msg, 1, false, false);
            this.tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                p.hand.moveToDeck(c, false);
            }
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;

            addToTop(new BuffProbabilityAction(probIncrease));
            addToTop(new DrawCardAction(p, drawAmount));
            this.isDone = true;
        }
    }

    private static class BuffProbabilityAction extends AbstractGameAction {
        private float increaseAmount;

        public BuffProbabilityAction(float increaseAmount) {
            this.increaseAmount = increaseAmount;
        }

        @Override
        public void update() {
            for (AbstractCard c : DrawCardAction.drawnCards) {
                if (c instanceof IProbabilityCard) {
                    ((IProbabilityCard) c).increaseBaseChance(increaseAmount);
                    c.superFlash();
                }
            }
            this.isDone = true;
        }
    }
}