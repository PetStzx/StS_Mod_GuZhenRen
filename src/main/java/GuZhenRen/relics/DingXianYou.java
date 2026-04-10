package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class DingXianYou extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("DingXianYou");
    private static final String IMG = GuZhenRen.assetPath("img/relics/DingXianYou.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/DingXianYou.png");

    public DingXianYou() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.BOSS, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new DingXianYou();
    }
}