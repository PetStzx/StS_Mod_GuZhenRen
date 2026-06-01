package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Iterator;

public class FengXiongHuaJi extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("FengXiongHuaJi");
    private static final String IMG = GuZhenRen.assetPath("img/relics/FengXiongHuaJi.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/FengXiongHuaJi.png");

    private final ArrayList<AbstractCard> pendingTransform = new ArrayList<>();

    public FengXiongHuaJi() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.UNCOMMON, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onObtainCard(AbstractCard card) {
        if (card.color == AbstractCard.CardColor.CURSE) {
            this.pendingTransform.add(card);
        }
    }

    @Override
    public void update() {
        super.update();

        if (!this.pendingTransform.isEmpty() && AbstractDungeon.player != null) {
            Iterator<AbstractCard> i = this.pendingTransform.iterator();
            while (i.hasNext()) {
                AbstractCard card = i.next();

                if (AbstractDungeon.player.masterDeck.contains(card)) {
                    this.flash();

                    card.untip();
                    card.unhover();
                    AbstractDungeon.player.masterDeck.removeCard(card);

                    AbstractCard.CardColor originalColor = card.color;
                    card.color = AbstractDungeon.player.getCardColor();
                    AbstractDungeon.transformCard(card, false, AbstractDungeon.miscRng);
                    card.color = originalColor;

                    if (AbstractDungeon.transformedCard != null) {
                        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(
                                AbstractDungeon.transformedCard,
                                Settings.WIDTH / 2.0F,
                                Settings.HEIGHT / 2.0F,
                                false
                        ));
                    }

                    i.remove();
                }
            }
        }
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        this.pendingTransform.clear();
    }

    @Override
    public AbstractRelic makeCopy() {
        return new FengXiongHuaJi();
    }
}