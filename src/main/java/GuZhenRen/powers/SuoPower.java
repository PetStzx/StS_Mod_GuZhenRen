package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

public class SuoPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("SuoPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int THRESHOLD = 7;

    public SuoPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 0;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/SuoPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/SuoPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.flashWithoutSound();
        this.amount++;

        if (this.amount >= THRESHOLD) {
            this.amount = 0;

            this.flash();
            CardCrawlGame.sound.play("POWER_SHACKLE");

            Color cyanGreen = new Color(0.2F, 1.0F, 0.5F, 1.0F);
            AbstractDungeon.effectsQueue.add(new ShockWaveEffect(this.owner.hb.cX, this.owner.hb.cY, cyanGreen, ShockWaveEffect.ShockWaveType.CHAOTIC));

            this.addToBot(new ApplyPowerAction(
                    AbstractDungeon.player,
                    this.owner,
                    new DexterityPower(AbstractDungeon.player, -1),
                    -1
            ));
        }
        this.updateDescription();
    }
}