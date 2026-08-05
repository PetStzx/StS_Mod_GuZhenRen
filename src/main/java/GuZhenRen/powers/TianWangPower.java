package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.EntanglePower;

public class TianWangPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("TianWangPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public TianWangPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 3;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/TianWangPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/TianWangPower.png");
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner != null && !owner.isDeadOrEscaped() && !owner.halfDead && !owner.isDying) {
                    AbstractDungeon.actionManager.addToTop(
                            new ApplyPowerAction(AbstractDungeon.player, owner, new EntanglePower(AbstractDungeon.player))
                    );
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void updateDescription() {
        if (this.amount > 0) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[2];
        }
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (this.amount > 0) {
            this.flash();
            this.amount--;

            if (this.amount == 0) {
                CardCrawlGame.sound.play("UNLOCK");
                this.addToBot(new RemoveSpecificPowerAction(AbstractDungeon.player, this.owner, "Entangled"));
            }
            this.updateDescription();
        }
    }

    @Override
    public void duringTurn() {
        if (this.owner == null || this.owner.isDeadOrEscaped() || this.owner.halfDead) {
            return;
        }

        this.flash();
        this.addToBot(new TianWangAction(this.owner, this));
    }

    public static class TianWangAction extends AbstractGameAction {
        private final AbstractPower power;

        public TianWangAction(AbstractCreature target, AbstractPower power) {
            this.target = target;
            this.power = power;
            this.actionType = ActionType.SPECIAL;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                if (this.target != null && !this.target.isDeadOrEscaped() && !this.target.halfDead && !this.target.isDying) {
                    AbstractDungeon.actionManager.addToTop(
                            new ApplyPowerAction(AbstractDungeon.player, this.target, new EntanglePower(AbstractDungeon.player))
                    );
                    this.power.amount = 3;
                    this.power.updateDescription();
                }
            }
            this.tickDuration();
        }
    }
}