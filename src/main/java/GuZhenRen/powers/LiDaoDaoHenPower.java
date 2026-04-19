package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
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

    private void changeCompanionStrength(int amt) {

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

    // 获得时增加力量
    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        changeCompanionStrength(this.amount);
    }

    // 叠加时增加力量
    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        changeCompanionStrength(stackAmount);
    }

    @Override
    public void reducePower(int reduceAmount) {
        int actualReduce = Math.min(reduceAmount, this.amount);
        changeCompanionStrength(-actualReduce);
        super.reducePower(reduceAmount);
    }

    @Override
    public void onRemove() {
        if (this.amount > 0) {
            changeCompanionStrength(-this.amount);
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