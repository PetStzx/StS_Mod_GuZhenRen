package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class FenShaoPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("FenShaoPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FenShaoPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;
        this.loadRegion("flameBarrier");
        this.updateDescription();
    }

    @Override
    public void onInitialApplication() {
        triggerBurningDamage(this.amount);
        triggerXingHuo(this.amount);
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;

        triggerBurningDamage(this.amount);
        if (stackAmount > 0) {
            triggerXingHuo(stackAmount);
        }
        this.updateDescription();
    }

    @Override
    public void reducePower(int reduceAmount) {
        super.reducePower(reduceAmount);
        if (this.amount > 0) {
            triggerBurningDamage(this.amount);
        }
        this.updateDescription();
    }

    @Override
    public void duringTurn() {
        this.addToBot(new FenShaoHalveAction(this.owner, this));
    }

    private void triggerBurningDamage(int damageAmount) {
        if (this.owner != null && !this.owner.isDeadOrEscaped() && damageAmount > 0) {
            this.flash();
            this.addToBot(new FenShaoDamageAction(this.owner, damageAmount));
        }
    }

    private void triggerXingHuo(int amountApplied) {
        if (this.owner != null && !this.owner.isDeadOrEscaped()) {
            if (this.owner.hasPower(XingHuoLiaoYuanPower.POWER_ID)) {
                if (!XingHuoLiaoYuanPower.isSpreading) {
                    ((XingHuoLiaoYuanPower)this.owner.getPower(XingHuoLiaoYuanPower.POWER_ID)).triggerSpread(amountApplied);
                }
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    public static class FenShaoDamageAction extends AbstractGameAction {
        public FenShaoDamageAction(AbstractCreature target, int damageAmount) {
            this.target = target;
            this.amount = damageAmount;
            this.actionType = ActionType.DAMAGE;
            this.duration = 0.1F;
        }

        @Override
        public void update() {
            if (this.duration == 0.1F && this.target != null && !this.target.isDeadOrEscaped()) {
                AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.FIRE));
                this.target.damage(new DamageInfo(AbstractDungeon.player, this.amount, DamageInfo.DamageType.THORNS));

                if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.clearPostCombatActions();
                }
            }
            this.tickDuration();
        }
    }

    public static class FenShaoHalveAction extends AbstractGameAction {
        private final AbstractPower power;

        public FenShaoHalveAction(AbstractCreature target, AbstractPower power) {
            this.target = target;
            this.power = power;
            this.actionType = ActionType.REDUCE_POWER;
            this.duration = 0.1F;
        }

        @Override
        public void update() {
            if (this.duration == 0.1F && this.target != null && !this.target.isDeadOrEscaped()) {
                int targetAmount = this.power.amount / 2;
                int reduceAmount = this.power.amount - targetAmount;

                if (reduceAmount > 0) {
                    if (targetAmount <= 0) {
                        this.target.powers.remove(this.power);
                        AbstractDungeon.onModifyPower();
                    } else {
                        this.power.reducePower(reduceAmount);
                        AbstractDungeon.onModifyPower();
                    }
                }
            }
            this.tickDuration();
        }
    }
}