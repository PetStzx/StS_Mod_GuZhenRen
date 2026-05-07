package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.ChouHenGuPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class WanJie_ChouHenGu extends AbstractTribulation {

    public WanJie_ChouHenGu() {
        super(
                GuZhenRen.makeID("WanJie_ChouHenGu"),
                "仇恨蛊",
                TribulationManager.TRIBULATION_TEXT[3],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new ChouHenGuPower(target, 0));
    }
}