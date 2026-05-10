package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.TieBiPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TianJie_TieBi extends AbstractTribulation {

    public TianJie_TieBi() {
        super(
                GuZhenRen.makeID("TianJie_TieBi"),
                "铁壁",
                TribulationManager.TRIBULATION_TEXT[1],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new TieBiPower(target, 12));
    }
}