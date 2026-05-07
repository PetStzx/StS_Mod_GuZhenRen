package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.SuoPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoJie_Suo extends AbstractTribulation {

    public HaoJie_Suo() {
        super(
                GuZhenRen.makeID("HaoJie_Suo"),
                "锁",
                TribulationManager.TRIBULATION_TEXT[2],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new SuoPower(target));
    }
}