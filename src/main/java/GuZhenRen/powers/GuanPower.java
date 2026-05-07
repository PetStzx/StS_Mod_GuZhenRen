package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

public class GuanPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("GuanPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int threshold;

    public GuanPower(AbstractCreature owner, int threshold) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.threshold = Math.max(1, threshold);
        this.amount = this.threshold;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/GuanPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/GuanPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.threshold + DESCRIPTIONS[1];
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if (!AbstractDungeon.actionManager.turnHasEnded && damageAmount > 0) {

            this.amount -= damageAmount;

            if (this.amount <= 0) {
                this.amount = this.threshold;
                this.updateDescription();

                this.flash();

                Color cyanGray = new Color(0.4F, 0.6F, 0.6F, 1.0F);
                AbstractDungeon.effectsQueue.add(new BorderFlashEffect(cyanGray, true));
                AbstractDungeon.effectsQueue.add(new ShockWaveEffect(this.owner.hb.cX, this.owner.hb.cY, cyanGray, ShockWaveEffect.ShockWaveType.CHAOTIC));
                AbstractDungeon.actionManager.callEndTurnEarlySequence();
            }
        }
    }
}