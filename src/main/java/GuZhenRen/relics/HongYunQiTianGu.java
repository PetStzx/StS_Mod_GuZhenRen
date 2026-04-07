package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.IProbabilityModifier;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class HongYunQiTianGu extends CustomRelic implements IProbabilityModifier {
    public static final String ID = GuZhenRen.makeID("HongYunQiTianGu");
    private static final String IMG = GuZhenRen.assetPath("img/relics/HongYunQiTianGu.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/HongYunQiTianGu.png");

    public HongYunQiTianGu() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public float getFinalProbabilityMultiplier(AbstractCard card) {
        // 每次翻倍概率时闪烁遗物，提供视觉反馈
        this.flash();
        return 2.0f;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new HongYunQiTianGu();
    }
}