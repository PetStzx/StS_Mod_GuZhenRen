package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.HongLeiGuPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TianJie_HongLeiGu extends AbstractTribulation {

    public TianJie_HongLeiGu() {
        super(
                GuZhenRen.makeID("TianJie_HongLeiGu"),
                "轰雷蛊",
                TribulationManager.TRIBULATION_TEXT[1],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new HongLeiGuPower(target, 3));
    }
}