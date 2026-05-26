package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class GuiBuJue extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("GuiBuJue");
    private static final String IMG = "GuiBuJue.png";
    private static final String OUTLINE = "GuiBuJue.png";

    public GuiBuJue() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.SPECIAL,
                LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}