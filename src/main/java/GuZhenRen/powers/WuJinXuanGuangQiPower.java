package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.SlowPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class WuJinXuanGuangQiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("WuJinXuanGuangQiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public WuJinXuanGuangQiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/WuJinXuanGuangQiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/WuJinXuanGuangQiPower.png");
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

    private void triggerStrengthLoss() {
        if (!this.owner.isDeadOrEscaped() && !this.owner.halfDead) {
            this.flash();
            if (!this.owner.hasPower("Artifact")) {
                this.addToBot(new ApplyPowerAction(this.owner, this.owner, new GainStrengthPower(this.owner, this.amount), this.amount));
            }
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -this.amount), -this.amount));
        }
    }

    @Override
    public void onInitialApplication() {
        if (this.owner.hasPower(SlowPower.POWER_ID)) {
            triggerStrengthLoss();
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (this.owner.hasPower(SlowPower.POWER_ID)) {
            triggerStrengthLoss();
        }
    }
}