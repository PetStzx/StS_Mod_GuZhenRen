package GuZhenRen.actions;

import GuZhenRen.cards.XueKuangGu;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class XueKuangGuAction extends AbstractGameAction {
    private AbstractCard leftCard;
    private AbstractCard rightCard;

    public XueKuangGuAction(AbstractCard leftCard, AbstractCard rightCard) {
        this.leftCard = leftCard;
        this.rightCard = rightCard;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {

            // 判定左侧卡牌是否合法
            if (this.leftCard != null && AbstractDungeon.player.hand.contains(this.leftCard)) {
                if (!CardModifierManager.hasModifier(this.leftCard, XueKuangGu.XueKuangModifier.MODIFIER_ID)) {
                    CardModifierManager.addModifier(this.leftCard, new XueKuangGu.XueKuangModifier());
                    this.leftCard.superFlash(Color.SCARLET.cpy());
                }
            }

            // 判定右侧卡牌是否合法
            if (this.rightCard != null && AbstractDungeon.player.hand.contains(this.rightCard)) {
                if (!CardModifierManager.hasModifier(this.rightCard, XueKuangGu.XueKuangModifier.MODIFIER_ID)) {
                    CardModifierManager.addModifier(this.rightCard, new XueKuangGu.XueKuangModifier());
                    this.rightCard.superFlash(Color.SCARLET.cpy());
                }
            }
        }
        this.isDone = true;
    }
}