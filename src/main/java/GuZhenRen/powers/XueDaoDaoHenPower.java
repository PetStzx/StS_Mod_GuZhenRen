package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class XueDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("XueDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private static final float LIFESTEAL_PER_STACK = 0.01F;

    public XueDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && target != this.owner && info.type == DamageInfo.DamageType.NORMAL) {

            int unblockedDamage = damageAmount - target.currentBlock;

            if (unblockedDamage > 0) {
                int healAmount = MathUtils.ceil(unblockedDamage * (this.amount * LIFESTEAL_PER_STACK));

                if (healAmount > 0) {
                    this.flash();
                    this.addToTop(new HealAction(this.owner, this.owner, healAmount));
                }
            }
        }
    }
}