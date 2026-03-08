package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class MuDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("MuDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public MuDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        // 计算提升的百分比 (例如 3层 = 45)
        int bonus = this.amount * 15;
        this.description = powerStrings.DESCRIPTIONS[0] + bonus + powerStrings.DESCRIPTIONS[1];
    }


    @Override
    public int onHeal(int healAmount) {
        if (healAmount > 0) {
            this.flash();

            // 每层提升 15% (0.15F) 的回复量
            float multiplier = 1.0F + (this.amount * 0.15F);

            // 计算结果四舍五入并返回给原版引擎
            return MathUtils.round((float)healAmount * multiplier);
        }
        return healAmount;
    }
}