package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.IProbabilityCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ZhuanYunPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("ZhuanYunPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ZhuanYunPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/ZhuanYunPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/ZhuanYunPower.png");

        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);


        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    public void onProbabilityRollFailed(AbstractCard card) {
        if (card instanceof IProbabilityCard) {
            this.flash();

            float increaseAmount = this.amount / 100.0f;
            ((IProbabilityCard) card).increaseBaseChance(increaseAmount);

            // 让卡牌闪烁一下
            card.superFlash();
        }
    }
}