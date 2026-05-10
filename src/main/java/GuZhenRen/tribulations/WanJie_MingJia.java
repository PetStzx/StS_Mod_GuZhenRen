package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.MingJiaPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class WanJie_MingJia extends AbstractTribulation {

    public WanJie_MingJia() {
        super(
                GuZhenRen.makeID("WanJie_MingJia"),
                "命甲",
                TribulationManager.TRIBULATION_TEXT[3],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new MingJiaPower(target));
    }
}