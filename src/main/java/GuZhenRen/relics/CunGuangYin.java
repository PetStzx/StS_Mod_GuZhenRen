package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class CunGuangYin extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("CunGuangYin");
    private static final String IMG = "CunGuangYin.png";
    private static final String OUTLINE = "CunGuangYin.png";

    public CunGuangYin() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.RARE,
                LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}