package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TianQiKuiSanPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("TianQiKuiSanPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public TianQiKuiSanPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.DEBUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/TianQiKuiSanPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/TianQiKuiSanPower.png");

        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void onInitialApplication() {
        checkSynthesis();
    }

    @Override
    public void atStartOfTurn() {
        this.addToBot(new LoseHPAction(this.owner, this.owner, 6));
        this.flash();
    }

    private void checkSynthesis() {
        if (this.owner.hasPower(RenQiKuiSanPower.POWER_ID) &&
                this.owner.hasPower(DiQiKuiSanPower.POWER_ID) &&
                this.owner.hasPower(POWER_ID)) {

            this.addToTop(new ApplyPowerAction(this.owner, this.owner, new XianQiaoBengKuiPower(this.owner, 5)));
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, DiQiKuiSanPower.POWER_ID));
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, RenQiKuiSanPower.POWER_ID));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}