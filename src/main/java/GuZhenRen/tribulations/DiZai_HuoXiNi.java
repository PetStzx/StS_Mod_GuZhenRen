package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.HuoXiNiPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DiZai_HuoXiNi extends AbstractTribulation {

    public DiZai_HuoXiNi() {
        super(
                GuZhenRen.makeID("DiZai_HuoXiNi"),
                "和稀泥",
                TribulationManager.TRIBULATION_TEXT[0],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new HuoXiNiPower(target, 1));
    }
}