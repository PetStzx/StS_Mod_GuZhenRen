package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.MuMeiGuPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TianJie_MuMeiGu extends AbstractTribulation {

    public TianJie_MuMeiGu() {
        super(
                GuZhenRen.makeID("TianJie_MuMeiGu"),
                "木魅蛊",
                TribulationManager.TRIBULATION_TEXT[1],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new MuMeiGuPower(target));
    }
}