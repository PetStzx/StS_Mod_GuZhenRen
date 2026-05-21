package GuZhenRen.actions;

import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.HuaShiGu;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class HuaShiAction extends AbstractGameAction {

    public HuaShiAction(int amount) {
        this.amount = amount;
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (AbstractDungeon.player.hand.isEmpty()) {
                this.isDone = true;
                return;
            }
            if (AbstractDungeon.player.hand.size() == 1) {
                applyFossil(AbstractDungeon.player.hand.getBottomCard());
                this.isDone = true;
                return;
            }
            // 直接调用化石蛊卡牌的扩展描述文本
            AbstractDungeon.handCardSelectScreen.open(HuaShiGu.cardStrings.EXTENDED_DESCRIPTION[0], 1, false, false);
            this.tickDuration();
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                applyFossil(c);
                AbstractDungeon.player.hand.addToTop(c);
            }
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            this.isDone = true;
        }
    }

    private void applyFossil(AbstractCard c) {
        HuaShiGu.HuaShiModifier mod = null;
        for (basemod.abstracts.AbstractCardModifier m : CardModifierManager.modifiers(c)) {
            if (m instanceof HuaShiGu.HuaShiModifier) {
                mod = (HuaShiGu.HuaShiModifier) m;
                break;
            }
        }

        if (mod != null) {
            mod.amount += this.amount;
            c.initializeDescription();
        } else {
            CardModifierManager.addModifier(c, new HuaShiGu.HuaShiModifier(this.amount));
        }

        c.superFlash();

        if (c instanceof AbstractGuZhenRenCard) {
            ((AbstractGuZhenRenCard) c).changeDao(AbstractGuZhenRenCard.Dao.TU_DAO);
        }
    }
}