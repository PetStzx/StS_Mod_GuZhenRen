package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class FeiLiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("FeiLiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FeiLiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/FeiLiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/FeiLiPower.png");
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
    public void duringTurn() {
        this.flash();
        this.addToBot(new FeiLiAction(this.owner, this));
    }


    public static class FeiLiAction extends AbstractGameAction {
        private final AbstractPower power;

        public FeiLiAction(AbstractCreature target, AbstractPower power) {
            this.target = target;
            this.power = power;
            this.actionType = ActionType.SPECIAL;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                if (this.target.hasPower(ArtifactPower.POWER_ID)) {
                    CardCrawlGame.sound.play("NULLIFY_SFX");

                    AbstractDungeon.actionManager.addToTop(
                            new com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction(
                                    this.target,
                                    com.megacrit.cardcrawl.actions.common.ApplyPowerAction.TEXT[0]
                            )
                    );

                    this.target.getPower(ArtifactPower.POWER_ID).onSpecificTrigger();
                } else {
                    CardCrawlGame.sound.play("POWER_STRENGTH", 0.05F);
                    AbstractPower strength = this.target.getPower(StrengthPower.POWER_ID);
                    if (strength != null) {
                        strength.stackPower(-1);
                        strength.updateDescription();
                    } else {
                        AbstractPower newStrength = new StrengthPower(this.target, -1);
                        this.target.powers.add(newStrength);
                        newStrength.onInitialApplication();
                    }
                }

                this.power.amount--;
                if (this.power.amount <= 0) {
                    this.target.powers.remove(this.power);
                } else {
                    this.power.updateDescription();
                }
            }

            this.tickDuration();
        }
    }
}