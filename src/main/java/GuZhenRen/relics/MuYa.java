package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.TianYuanBaoLian;
import GuZhenRen.cards.MuJiaGu;
import GuZhenRen.cards.JiuYeShengJiCao;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public class MuYa extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("MuYa");
    private static final String IMG = GuZhenRen.assetPath("img/relics/MuYa.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/MuYa.png");

    private static final int THRESHOLD = 10;

    public MuYa() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.RARE, LandingSound.MAGICAL);
        this.counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + THRESHOLD + DESCRIPTIONS[1];
    }


    @Override
    public void onExhaust(AbstractCard card) {
        this.counter++;

        if (this.counter >= THRESHOLD) {
            this.counter -= THRESHOLD;
            this.flash();

            AbstractCard c = getRandomMuDaoCard();
            c.modifyCostForCombat(-99);

            this.addToBot(new MakeTempCardInHandAction(c, 1));
        }

        checkPulse();
    }

    @Override
    public void atBattleStart() {
        checkPulse();
    }

    private void checkPulse() {
        if (this.counter >= THRESHOLD - 1) {
            this.beginPulse();
            this.pulse = true;
        } else {
            this.pulse = false;
        }
    }


    private AbstractCard getRandomMuDaoCard() {
        ArrayList<AbstractCard> muDaoPool = new ArrayList<>();

        muDaoPool.add(new TianYuanBaoLian());
        muDaoPool.add(new MuJiaGu());
        muDaoPool.add(new JiuYeShengJiCao());

        int index = AbstractDungeon.cardRandomRng.random(muDaoPool.size() - 1);
        return muDaoPool.get(index).makeCopy();
    }

    @Override
    public AbstractRelic makeCopy() {
        return new MuYa();
    }
}