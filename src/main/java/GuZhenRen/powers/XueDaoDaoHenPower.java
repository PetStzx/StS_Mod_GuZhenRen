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

    // 每层 5% 的生命汲取
    private static final float LIFESTEAL_PER_STACK = 0.05F;

    public XueDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        // 计算显示的百分比 (例如 2层 = 10%)
        int percentage = (int)(this.amount * LIFESTEAL_PER_STACK * 100);
        this.description = powerStrings.DESCRIPTIONS[0] + percentage + powerStrings.DESCRIPTIONS[1];
    }

    // =========================================================================
    // 【核心逻辑】 造成攻击伤害时触发吸血
    // =========================================================================
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        // 确保是普通攻击伤害，目标不是自己，且伤害值大于 0
        if (damageAmount > 0 && target != this.owner && info.type == DamageInfo.DamageType.NORMAL) {

            // 向上取整计算
            int healAmount = MathUtils.ceil(damageAmount * (this.amount * LIFESTEAL_PER_STACK));

            if (healAmount > 0) {
                this.flash();
                // 将治疗动作排入队列，在造成伤害后立刻回血
                this.addToTop(new HealAction(this.owner, this.owner, healAmount));
            }
        }
    }
}