package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.LeiDianGuPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class WanJie_LeiDianGu extends AbstractTribulation {

    public WanJie_LeiDianGu() {
        super(
                GuZhenRen.makeID("WanJie_LeiDianGu"),
                "雷电蛊",
                TribulationManager.TRIBULATION_TEXT[3],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new LeiDianGuPower(target, 3));
    }
}