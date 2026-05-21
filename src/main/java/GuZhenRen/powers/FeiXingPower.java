package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class FeiXingPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("FeiXingPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Flight");

    public FeiXingPower(AbstractCreature owner, int amount) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        this.loadRegion("flight");
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && this.amount > 0) {
            return damage * 0.5F;
        }
        return damage;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && damageAmount > 0) {
            this.flash();
            this.addToTop(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
        }
        return damageAmount;
    }

    @Override
    public void onRemove() {
        if (this.owner.hasPower(ZhenChiGaoFeiPower.POWER_ID)) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, ZhenChiGaoFeiPower.POWER_ID));
        }
    }
}