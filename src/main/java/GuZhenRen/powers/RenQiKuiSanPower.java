package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class RenQiKuiSanPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("RenQiKuiSanPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public RenQiKuiSanPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.DEBUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/RenQiKuiSanPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/RenQiKuiSanPower.png");

        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void onInitialApplication() {
        AbstractDungeon.player.gameHandSize -= 1;
        checkSynthesis();
    }

    @Override
    public void onRemove() {
        AbstractDungeon.player.gameHandSize += 1;
    }

    private void checkSynthesis() {
        if (this.owner.hasPower(POWER_ID) &&
                this.owner.hasPower(DiQiKuiSanPower.POWER_ID) &&
                this.owner.hasPower(TianQiKuiSanPower.POWER_ID)) {

            this.addToTop(new ApplyPowerAction(this.owner, this.owner, new XianQiaoBengKuiPower(this.owner, 5)));
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, TianQiKuiSanPower.POWER_ID));
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, DiQiKuiSanPower.POWER_ID));
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}