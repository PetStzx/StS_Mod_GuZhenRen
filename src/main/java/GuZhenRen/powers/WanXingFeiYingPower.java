package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class WanXingFeiYingPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("WanXingFeiYingPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int nianBaseAmount;

    public WanXingFeiYingPower(AbstractCreature owner, int turns, int nianBaseAmount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = turns;
        this.nianBaseAmount = nianBaseAmount;
        this.type = PowerType.BUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/WanXingFeiYingPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/WanXingFeiYingPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);

        if (largeTexture == null || smallTexture == null) {
            pathLarge = GuZhenRen.assetPath("img/powers/XingNianGuPower_p.png");
            pathSmall = GuZhenRen.assetPath("img/powers/XingNianGuPower.png");
            largeTexture = ImageMaster.loadImage(pathLarge);
            smallTexture = ImageMaster.loadImage(pathSmall);
        }

        if (largeTexture != null && smallTexture != null) {
            this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
            this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);
        } else {
            this.loadRegion("starlight");
        }

        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        this.flash();
        // 直接给予干净的 NianPower，固定为传进来的基础值
        this.addToBot(new ApplyPowerAction(owner, owner,
                new NianPower(owner, this.nianBaseAmount), this.nianBaseAmount));
    }

    @Override
    public void atEndOfRound() {
        this.amount--;
        if (this.amount == 0) {
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        } else {
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.nianBaseAmount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }
}