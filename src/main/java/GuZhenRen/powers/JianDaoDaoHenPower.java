package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class JianDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("JianDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public JianDaoDaoHenPower(AbstractCreature owner, int amount) {
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
        if (JianFengPower.POWER_ID.equals(powerID)) {
            return this.amount;
        }
        return 0;
    }


    // 发放剑锋
    private void applyCompanionJianFeng(int amt) {
        this.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDaoHenPower.isDerivedPower = false;
                this.isDone = true;
            }
        });

        // 发放剑锋
        this.addToTop(new ApplyPowerAction(this.owner, this.owner, new JianFengPower(this.owner, amt), amt));

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
        applyCompanionJianFeng(this.amount);
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        applyCompanionJianFeng(stackAmount);
    }

    @Override
    public void reducePower(int reduceAmount) {
        int actualReduce = Math.min(reduceAmount, this.amount);
        this.addToTop(new ReducePowerAction(this.owner, this.owner, JianFengPower.POWER_ID, actualReduce));
        super.reducePower(reduceAmount);
    }

    @Override
    public void onRemove() {
        if (this.amount > 0) {
            this.addToTop(new ReducePowerAction(this.owner, this.owner, JianFengPower.POWER_ID, this.amount));
        }
    }
}