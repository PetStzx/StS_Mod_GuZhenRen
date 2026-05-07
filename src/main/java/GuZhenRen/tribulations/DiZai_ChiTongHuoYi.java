package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.ChiTongHuoYiPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DiZai_ChiTongHuoYi extends AbstractTribulation {

    public DiZai_ChiTongHuoYi() {
        super(
                GuZhenRen.makeID("DiZai_ChiTongHuoYi"),
                "赤铜火蚁",
                TribulationManager.TRIBULATION_TEXT[0],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new ChiTongHuoYiPower(target, 4));
    }
}