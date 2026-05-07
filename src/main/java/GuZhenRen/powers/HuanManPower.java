package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HuanManPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("HuanManPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public HuanManPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;

        this.type = PowerType.DEBUFF;
        this.isTurnBased = false;
        this.loadRegion("slow");

        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        this.amount = 0;
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
        if (this.amount > 0) {
            this.description += DESCRIPTIONS[1] + (this.amount * 10) + DESCRIPTIONS[2];
        }
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.addToBot(new ApplyPowerAction(this.owner, this.owner, new HuanManPower(this.owner, 1), 1));
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * (1.0F + (float)this.amount * 0.1F);
        }
        return damage;
    }
}