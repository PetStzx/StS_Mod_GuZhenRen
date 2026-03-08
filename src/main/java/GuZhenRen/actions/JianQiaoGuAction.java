package GuZhenRen.actions;

import GuZhenRen.cards.JianQiaoGu;
import GuZhenRen.patches.GuZhenRenTags;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class JianQiaoGuAction extends AbstractGameAction {
    private AbstractPlayer p;
    private ArrayList<AbstractCard> cannotBuff = new ArrayList<>();
    private boolean anyCard;

    public JianQiaoGuAction(boolean anyCard) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.p = AbstractDungeon.player;
        this.duration = Settings.ACTION_DUR_FAST;
        this.anyCard = anyCard;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            for (AbstractCard c : this.p.hand.group) {
                if ((!this.anyCard && !c.hasTag(GuZhenRenTags.JIAN_DAO)) || CardModifierManager.hasModifier(c, JianQiaoGu.JianQiaoModifier.MODIFIER_ID)) {
                    this.cannotBuff.add(c);
                }
            }

            if (this.cannotBuff.size() == this.p.hand.group.size()) {
                this.isDone = true;
                return;
            }

            this.p.hand.group.removeAll(this.cannotBuff);

            if (this.p.hand.group.size() == 1) {
                AbstractCard c = this.p.hand.getTopCard();
                CardModifierManager.addModifier(c, new JianQiaoGu.JianQiaoModifier());
                c.superFlash();
                this.returnCards();
                this.isDone = true;
                return;
            }

            String msg = JianQiaoGu.cardStrings.EXTENDED_DESCRIPTION[0];
            AbstractDungeon.handCardSelectScreen.open(msg, 1, false, false, false, false);
            this.tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                CardModifierManager.addModifier(c, new JianQiaoGu.JianQiaoModifier());
                c.superFlash();
                this.p.hand.addToTop(c);
            }
            this.returnCards();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            this.isDone = true;
        }
        this.tickDuration();
    }

    private void returnCards() {
        for (AbstractCard c : this.cannotBuff) {
            this.p.hand.addToTop(c);
        }
        this.p.hand.refreshHandLayout();
    }
}