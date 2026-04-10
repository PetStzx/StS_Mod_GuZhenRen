package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.ThornsPower; // 引入原版荆棘

public class GuDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("GuDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public GuDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0];
    }

    // =========================================================================
    //  宣告保护额度
    // =========================================================================
    @Override
    public int getDerivedPowerAmount(String powerID) {
        if (ThornsPower.POWER_ID.equals(powerID)) {
            return this.amount * 2;
        }
        return 0;
    }

    // =========================================================================
    // 发放伴生荆棘，开启/关闭免检标记
    // =========================================================================
    private void applyCompanionThorns(int amt) {
        this.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDaoHenPower.isDerivedPower = false;
                this.isDone = true;
            }
        });

        // 发放 2 倍的荆棘
        int thornsToApply = amt * 2;
        this.addToTop(new ApplyPowerAction(this.owner, this.owner, new ThornsPower(this.owner, thornsToApply), thornsToApply));

        this.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDaoHenPower.isDerivedPower = true;
                this.isDone = true;
            }
        });
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        applyCompanionThorns(this.amount);
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        applyCompanionThorns(stackAmount);
    }

    // 减少层数时同步扣减荆棘
    @Override
    public void reducePower(int reduceAmount) {
        int actualReduce = Math.min(reduceAmount, this.amount);
        // 扣除 2 倍的荆棘
        this.addToTop(new ReducePowerAction(this.owner, this.owner, ThornsPower.POWER_ID, actualReduce * 2));
        super.reducePower(reduceAmount);
    }

    // 被彻底移除时扣除所有伴生荆棘
    @Override
    public void onRemove() {
        if (this.amount > 0) {
            this.addToTop(new ReducePowerAction(this.owner, this.owner, ThornsPower.POWER_ID, this.amount * 2));
        }
    }
}