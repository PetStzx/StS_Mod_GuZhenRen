package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class YanXinGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("YanXinGu");
    private static final String IMG = GuZhenRen.assetPath("img/relics/YanXinGu.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/YanXinGu.png");

    public YanXinGu() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.UNCOMMON, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }


    @Override
    public AbstractRelic makeCopy() {
        return new YanXinGu();
    }
}