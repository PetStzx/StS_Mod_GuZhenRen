package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

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
        // 按回合衰减的状态
        this.isTurnBased = true;
        this.loadRegion("flameBarrier");

        this.updateDescription();
    }

    // 1. 初次附着时
    @Override
    public void onInitialApplication() {
        triggerBurningDamage();
        triggerXingHuo(this.amount);
    }

    // 2. 层数增加时
    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;

        triggerBurningDamage();

        // 只有正向增加时，才触发星火燎原传染
        if (stackAmount > 0) {
            triggerXingHuo(stackAmount);
        }
        this.updateDescription();
    }

    // 3. 层数减少时
    @Override
    public void reducePower(int reduceAmount) {
        super.reducePower(reduceAmount);

        // 层数减少依然触发受伤
        if (this.amount > 0) {
            triggerBurningDamage();
        }
        this.updateDescription();
    }

    // 4. 每个实体的独立回合结束时的衰减逻辑
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        // 计算减半后的层数 (向下取整)
        int targetAmount = this.amount / 2;
        // 计算需要削减掉的层数
        int reduceAmount = this.amount - targetAmount;

        if (reduceAmount > 0) {
            if (targetAmount <= 0) {
                // 减半归零，直接移除该状态
                this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            } else {
                // 使用原版的削减能力Action，这会自动触发 reducePower() 从而造成伤害
                this.addToBot(new ReducePowerAction(this.owner, this.owner, this, reduceAmount));
            }
        }
    }

    // 造成伤害的底层方法
    private void triggerBurningDamage() {
        if (this.owner != null && !this.owner.isDeadOrEscaped() && this.amount > 0) {
            this.flash();
            this.addToBot(new DamageAction(
                    this.owner,
                    new DamageInfo(AbstractDungeon.player, this.amount, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.FIRE
            ));
        }
    }

    // 触发传染的底层方法
    private void triggerXingHuo(int amountApplied) {
        if (this.owner != null && !this.owner.isDeadOrEscaped()) {
            if (this.owner.hasPower(XingHuoLiaoYuanPower.POWER_ID)) {
                // 阻断锁：不在传染中才执行
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
}