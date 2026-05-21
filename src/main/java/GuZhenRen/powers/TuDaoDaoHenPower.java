package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class TuDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("TuDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private boolean isTriggering = false;

    public TuDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }

    @Override
    public void onGainedBlock(float blockAmount) {
        if (blockAmount > 0.0F && !this.isTriggering) {
            this.flash();

            this.isTriggering = true;

            this.addToTop(new AbstractGameAction() {
                @Override
                public void update() {
                    isTriggering = false;
                    this.isDone = true;
                }
            });

            this.addToTop(new GainBlockAction(this.owner, this.owner, this.amount));
        }
    }
}