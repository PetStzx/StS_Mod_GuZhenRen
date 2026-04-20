package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class LengXuePower extends AbstractPower implements HealthBarRenderPower {
    public static final String POWER_ID = GuZhenRen.makeID("LengXuePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Color ICE_BLUE = new Color(0.86F, 0.96F, 1.0F, 1.0F);
    public LengXuePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/LengXuePower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/LengXuePower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.flashWithoutSound();
            this.addToBot(new LengXueAction(this.owner, this));
        }
    }


    @Override
    public int getHealthBarAmount() {
        return Math.max(1, this.owner.maxHealth / 10);
    }

    @Override
    public Color getColor() {
        return ICE_BLUE;
    }

    public static class LengXueAction extends AbstractGameAction {
        private final AbstractPower power;

        public LengXueAction(AbstractCreature target, AbstractPower power) {
            this.target = target;
            this.power = power;
            this.actionType = ActionType.DAMAGE;
            this.duration = 0.33F;
        }

        @Override
        public void update() {
            if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
                this.isDone = true;
                return;
            }

            if (this.duration == 0.33F && this.target.currentHealth > 0) {
                this.power.flash();
            }

            this.tickDuration();

            if (this.isDone) {
                if (this.target.currentHealth > 0) {
                    this.target.tint.color = ICE_BLUE.cpy();
                    this.target.tint.changeColor(Color.WHITE.cpy());

                    int damage = Math.max(1, this.target.maxHealth / 10);
                    this.target.damage(new DamageInfo(this.target, damage, DamageInfo.DamageType.HP_LOSS));
                }

                AbstractPower p = this.target.getPower(LengXuePower.POWER_ID);
                if (p != null) {
                    p.amount--;
                    if (p.amount <= 0) {
                        this.target.powers.remove(p);
                    } else {
                        p.updateDescription();
                    }
                }

                if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.clearPostCombatActions();
                }

                this.addToTop(new WaitAction(0.1F));
            }
        }
    }
}