package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class FeiLiGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("FeiLiGu");
    private static final String IMG = "FeiLiGu.png";
    private static final String OUTLINE = "FeiLiGu.png";

    public FeiLiGu() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.RARE,
                LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}