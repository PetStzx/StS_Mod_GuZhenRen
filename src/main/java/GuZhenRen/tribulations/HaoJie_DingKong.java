package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.DingKongPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoJie_DingKong extends AbstractTribulation {

    public HaoJie_DingKong() {
        super(
                GuZhenRen.makeID("HaoJie_DingKong"),
                "定空",
                TribulationManager.TRIBULATION_TEXT[2],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new DingKongPower(target));
    }
}