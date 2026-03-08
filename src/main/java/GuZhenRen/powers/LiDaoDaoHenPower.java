package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LiDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("LiDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public LiDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0];
    }

    // 发放力量，同时开启/关闭免检标记
    private void applyCompanionStrength(int amt) {
        // addToTop 是后进先出，所以队列排布顺序是：关闭 -> 发放 -> 开启
        this.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDaoHenPower.isDerivedPower = false;
                this.isDone = true;
            }
        });

        this.addToTop(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, amt), amt));

        this.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDaoHenPower.isDerivedPower = true;
                this.isDone = true;
            }
        });
    }

    // 获得时发放力量
    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        applyCompanionStrength(this.amount);
    }

    // 叠加时发放力量
    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        applyCompanionStrength(stackAmount);
    }

    // 减少层数时同步扣减力量（防止原版 reducePower 导致双重扣减的严密逻辑）
    @Override
    public void reducePower(int reduceAmount) {
        int actualReduce = Math.min(reduceAmount, this.amount);
        this.addToTop(new ReducePowerAction(this.owner, this.owner, StrengthPower.POWER_ID, actualReduce));
        super.reducePower(reduceAmount);
    }

    // 被彻底移除时扣除力量
    @Override
    public void onRemove() {
        if (this.amount > 0) {
            this.addToTop(new ReducePowerAction(this.owner, this.owner, StrengthPower.POWER_ID, this.amount));
        }
    }

    @Override
    public int getDerivedPowerAmount(String powerID) {
        if (StrengthPower.POWER_ID.equals(powerID)) {
            return this.amount;
        }
        return 0;
    }
}