package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
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
        this.isTurnBased = false;
        this.loadRegion("flameBarrier");

        this.updateDescription();
    }

    @Override
    public void onInitialApplication() {
        triggerBurningDamage();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;

        this.amount += stackAmount;

        triggerBurningDamage();
        this.updateDescription();
    }

    private void triggerBurningDamage() {
        if (this.owner != null && !this.owner.isDeadOrEscaped()) {
            this.flash();
            this.addToBot(new LoseHPAction(this.owner, this.owner, this.amount, AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}