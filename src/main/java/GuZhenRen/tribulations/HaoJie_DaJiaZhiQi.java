package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.DaJiaZhiQiPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoJie_DaJiaZhiQi extends AbstractTribulation {

    public HaoJie_DaJiaZhiQi() {
        super(
                GuZhenRen.makeID("HaoJie_DaJiaZhiQi"),
                "大家之气",
                TribulationManager.TRIBULATION_TEXT[2],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new DaJiaZhiQiPower(target));
    }
}