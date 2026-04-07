package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.unique.RetainCardsAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class ZhiDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("ZhiDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private int energyRetained = 0;

    public ZhiDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1] + this.amount + powerStrings.DESCRIPTIONS[2];
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            if (!AbstractDungeon.player.hasRelic("Ice Cream")) {
                if (this.amount > 0) {
                    this.energyRetained = Math.min(EnergyPanel.totalCount, this.amount);
                } else {
                    this.energyRetained = 0;
                }
            }

            if (!AbstractDungeon.player.hand.isEmpty() && !AbstractDungeon.player.hasRelic("Runic Pyramid") && !AbstractDungeon.player.hasPower("Equilibrium")) {
                this.addToBot(new RetainCardsAction(this.owner, this.amount));
            }
        }
    }

    @Override
    public void atStartOfTurn() {
        super.atStartOfTurn();

        if (this.energyRetained > 0) {
            this.flash();
            this.addToBot(new GainEnergyAction(this.energyRetained));
            this.energyRetained = 0;
        }
    }
}