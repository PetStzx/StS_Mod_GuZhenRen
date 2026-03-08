package GuZhenRen.actions;

import GuZhenRen.cards.XueKuangGu;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.Collections;

public class XueKuangGuAction extends AbstractGameAction {
    private int amount;

    public XueKuangGuAction(int amount) {
        this.amount = amount;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            ArrayList<AbstractCard> eligibleCards = new ArrayList<>();
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (!CardModifierManager.hasModifier(c, XueKuangGu.XueKuangModifier.MODIFIER_ID)) {
                    eligibleCards.add(c);
                }
            }

            if (!eligibleCards.isEmpty()) {
                Collections.shuffle(eligibleCards, new java.util.Random(AbstractDungeon.cardRandomRng.randomLong()));
                int cardsToModify = Math.min(this.amount, eligibleCards.size());

                for (int i = 0; i < cardsToModify; i++) {
                    AbstractCard c = eligibleCards.get(i);

                    // 打词条和播放特效
                    CardModifierManager.addModifier(c, new XueKuangGu.XueKuangModifier());
                    c.superFlash(Color.SCARLET.cpy());
                }
            }
        }
        this.isDone = true;
    }
}