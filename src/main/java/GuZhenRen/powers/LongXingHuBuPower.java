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
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LongXingHuBuPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("LongXingHuBuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public LongXingHuBuPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/LongXingHuBuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/LongXingHuBuPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);

        if (largeTexture != null && smallTexture != null) {
            this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
            this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);
        } else {
            this.loadRegion("flex");
        }

        updateDescription();
    }

    //  打出牌时，获得力量和失去力量
    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        this.flash();
        // 1. 获得力量
        this.addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
        // 2. 获得“失去力量”
        this.addToBot(new ApplyPowerAction(this.owner, this.owner, new LoseStrengthPower(this.owner, this.amount), this.amount));
    }


    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}