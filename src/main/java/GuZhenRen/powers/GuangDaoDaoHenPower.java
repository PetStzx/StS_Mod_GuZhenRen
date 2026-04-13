package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class GuangDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("GuangDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final float MULTIPLIER_PER_STACK = 0.25F;
    public static final int PERCENT_PER_STACK = 25;

    public GuangDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        int bonus = this.amount * PERCENT_PER_STACK;
        this.description = powerStrings.DESCRIPTIONS[0] + bonus + powerStrings.DESCRIPTIONS[1];
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        if (type == DamageInfo.DamageType.NORMAL && card.hasTag(GuZhenRenTags.GUANG_DAO)) {
            if (this.owner.hasPower(ShanYaoPower.POWER_ID)) {
                return damage;
            }

            float multiplier = 1.0F + (this.amount * MULTIPLIER_PER_STACK);
            return damage * multiplier;
        }
        return damage;
    }
}