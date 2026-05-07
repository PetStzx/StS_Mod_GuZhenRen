package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.GuoDeQuPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoJie_GuoDeQu extends AbstractTribulation {

    public HaoJie_GuoDeQu() {
        super(
                GuZhenRen.makeID("HaoJie_GuoDeQu"),
                "过得去",
                TribulationManager.TRIBULATION_TEXT[2],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new GuoDeQuPower(target));
    }
}