package GuZhenRen.actions;

import GuZhenRen.cards.JianQiaoGu;
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

    public JianQiaoGuAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.p = AbstractDungeon.player;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            // 遍历手牌，把非攻击牌，或者已经带有剑鞘Buff的牌挑出来
            for (AbstractCard c : this.p.hand.group) {
                if (c.type != AbstractCard.CardType.ATTACK || CardModifierManager.hasModifier(c, JianQiaoGu.JianQiaoModifier.MODIFIER_ID)) {
                    this.cannotBuff.add(c);
                }
            }

            // 如果手牌里全是不能附魔的牌，直接结束
            if (this.cannotBuff.size() == this.p.hand.group.size()) {
                this.isDone = true;
                return;
            }

            // 临时移出不可选的牌
            this.p.hand.group.removeAll(this.cannotBuff);

            // 如果手牌里刚好只剩 1 张合法的攻击牌，自动选它
            if (this.p.hand.group.size() == 1) {
                AbstractCard c = this.p.hand.getTopCard();
                CardModifierManager.addModifier(c, new JianQiaoGu.JianQiaoModifier());
                // 将它塞回抽牌堆顶部
                this.p.hand.moveToDeck(c, false);

                this.returnCards();
                this.isDone = true;
                return;
            }

            // 呼出选牌界面
            String msg = JianQiaoGu.cardStrings.EXTENDED_DESCRIPTION[0];
            AbstractDungeon.handCardSelectScreen.open(msg, 1, false, false, false, false);
            this.tickDuration();
            return;
        }

        // 选完牌后的处理
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                // 赋予居合附魔
                CardModifierManager.addModifier(c, new JianQiaoGu.JianQiaoModifier());
                c.superFlash();
                // 必须先加回手牌，才能通过 moveToDeck 塞入抽牌堆
                this.p.hand.addToTop(c);
                this.p.hand.moveToDeck(c, false);
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