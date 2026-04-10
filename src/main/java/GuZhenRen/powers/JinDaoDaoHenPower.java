package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.MetallicizePower; // 导入原版的金属化能力

public class JinDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("JinDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public JinDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0];
    }

    // 宣告保护额度，防止被万物大同变清洗
    @Override
    public int getDerivedPowerAmount(String powerID) {
        if (MetallicizePower.POWER_ID.equals(powerID)) {
            return this.amount;
        }
        return 0;
    }

    // 发放伴生金属化
    private void applyCompanionMetallicize(int amt) {
        this.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDaoHenPower.isDerivedPower = false;
                this.isDone = true;
            }
        });

        // 发放金属化
        this.addToTop(new ApplyPowerAction(this.owner, this.owner, new MetallicizePower(this.owner, amt), amt));

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
        applyCompanionMetallicize(this.amount);
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        applyCompanionMetallicize(stackAmount);
    }

    @Override
    public void reducePower(int reduceAmount) {
        int actualReduce = Math.min(reduceAmount, this.amount);
        this.addToTop(new ReducePowerAction(this.owner, this.owner, MetallicizePower.POWER_ID, actualReduce));
        super.reducePower(reduceAmount);
    }

    @Override
    public void onRemove() {
        if (this.amount > 0) {
            this.addToTop(new ReducePowerAction(this.owner, this.owner, MetallicizePower.POWER_ID, this.amount));
        }
    }
}