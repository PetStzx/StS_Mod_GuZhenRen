package GuZhenRen.actions;

import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ShanJianGuAction extends AbstractGameAction {
    private boolean hasDrawn = false; // 标记是否已经执行过抽牌

    public ShanJianGuAction(int amount) {
        this.amount = amount;
        this.actionType = ActionType.DRAW;

        // 根据玩家是否开启了“快速模式”，设置适当的动画间隔时长
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FASTER;
        }
    }

    @Override
    public void update() {
        // 1. 只在动作刚开始的那一帧，执行物理上的抽牌与判定逻辑
        if (!this.hasDrawn) {
            this.hasDrawn = true; // 锁定，防止下一帧重复执行

            if (AbstractDungeon.player.hasPower("No Draw")) {
                AbstractDungeon.player.getPower("No Draw").flash();
                this.isDone = true;
                return;
            }

            int deckSize = AbstractDungeon.player.drawPile.size();
            int discardSize = AbstractDungeon.player.discardPile.size();

            if (deckSize + discardSize == 0) {
                this.isDone = true;
                return;
            }

            if (deckSize == 0) {
                this.addToTop(new ShanJianGuAction(this.amount));
                this.addToTop(new EmptyDeckShuffleAction());
                this.isDone = true;
                return;
            }

            AbstractCard c = AbstractDungeon.player.drawPile.getTopCard();

            // 引擎执行物理抽牌
            AbstractDungeon.player.draw();
            AbstractDungeon.player.hand.refreshHandLayout();

            // 判定连抽
            if (c.hasTag(GuZhenRenTags.JIAN_DAO) && AbstractDungeon.player.hand.contains(c)) {
                this.amount++;
            }

            this.amount--;

            // 如果还有剩余次数，排入队列。因为使用了 addToTop，下一个动作会紧接着当前动作执行
            if (this.amount > 0) {
                this.addToTop(new ShanJianGuAction(this.amount));
            }
        }

        // 2. 让动作随时间流逝逐渐减少 duration，
        // 直到 duration <= 0 时，引擎底层会自动把 this.isDone 设为 true。
        // 为卡牌的飞行留足了动画时间
        this.tickDuration();
    }
}