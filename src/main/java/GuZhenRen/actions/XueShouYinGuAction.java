package GuZhenRen.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XueShouYinGuAction extends AbstractGameAction {
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString("ExhaustAction").TEXT;
    private AbstractMonster m;

    public XueShouYinGuAction(AbstractMonster target) {
        this.m = target;
        this.actionType = ActionType.EXHAUST;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (AbstractDungeon.player.hand.isEmpty()) {
                this.isDone = true;
                return;
            }
            AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false, false);
            this.tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                if (c.type == AbstractCard.CardType.ATTACK) {
                    AbstractDungeon.player.limbo.addToBottom(c);
                    c.exhaustOnUseOnce = true;

                    AbstractMonster targetMonster = this.m;
                    if (targetMonster == null || targetMonster.isDeadOrEscaped()) {
                        targetMonster = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                    }

                    if (targetMonster != null) {
                        c.calculateCardDamage(targetMonster);
                    }

                    AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(c, targetMonster, c.energyOnUse, true, true), true);
                } else {
                    AbstractDungeon.player.hand.moveToExhaustPile(c);
                }
            }
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            this.isDone = true;
        }
    }
}