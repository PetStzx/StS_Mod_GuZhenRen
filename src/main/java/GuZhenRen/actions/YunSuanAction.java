package GuZhenRen.actions;

import GuZhenRen.util.IProbabilityCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class YunSuanAction extends AbstractGameAction {
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
                // 【修复报错】分开压入队列。addToTop是后进先出，所以先压入回调，再压入抽牌
                addToTop(new BuffProbabilityAction(probIncrease));
                addToTop(new DrawCardAction(p, drawAmount));
                this.isDone = true;
                return;
            }
            AbstractDungeon.handCardSelectScreen.open("放到抽牌堆顶部", 1, false, false);
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

    // 内部类：抽牌动作完成后的回调回调机制
    private static class BuffProbabilityAction extends AbstractGameAction {
        private float increaseAmount;

        public BuffProbabilityAction(float increaseAmount) {
            this.increaseAmount = increaseAmount;
        }

        @Override
        public void update() {
            // DrawCardAction.drawnCards 记录了刚刚被抽上来的牌
            for (AbstractCard c : DrawCardAction.drawnCards) {
                if (c instanceof IProbabilityCard) {
                    // 如果这牌实现了我们的接口，直接修改其属性
                    ((IProbabilityCard) c).increaseBaseChance(increaseAmount);
                    c.superFlash(); // 闪烁一下，给予玩家直观的视觉反馈
                }
            }
            this.isDone = true;
        }
    }
}