package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractXuYingCard;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.util.ArrayList;

public class NongXuGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("NongXuGu");
    private static final String IMG = "NongXuGu.png";
    private static final String OUTLINE = "NongXuGu.png";

    private static ArrayList<AbstractCard> phantomCardPool = null;

    public NongXuGu() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.UNCOMMON,
                LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStartPreDraw() {
        this.flash();
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

        AbstractCard randomXuYing = getRandomXuYingCard();
        if (randomXuYing != null) {
            this.addToBot(new MakeTempCardInHandAction(randomXuYing, 1, false));
        }
    }

    private AbstractCard getRandomXuYingCard() {
        if (phantomCardPool == null) {
            phantomCardPool = new ArrayList<>();
            for (AbstractCard c : CardLibrary.getAllCards()) {
                if (c instanceof AbstractXuYingCard) {
                    phantomCardPool.add(c);
                }
            }
        }

        if (!phantomCardPool.isEmpty()) {
            int randomIndex = AbstractDungeon.cardRandomRng.random(phantomCardPool.size() - 1);
            return phantomCardPool.get(randomIndex).makeCopy();
        }

        return null;
    }
}