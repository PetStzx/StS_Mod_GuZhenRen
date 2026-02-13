package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.DrawReductionPower;

public class SheXinPower extends DrawReductionPower {
    public static final String POWER_ID = GuZhenRen.makeID("SheXinPower");

    public SheXinPower(AbstractCreature owner, int amount) {
        super(owner, amount);
        this.ID = POWER_ID;
    }

    @Override
    public void atStartOfTurnPostDraw() {
        addToBot(new ReducePowerAction(owner, owner, POWER_ID, 1));
    }

    @Override
    public void atEndOfRound() {
    }
}
