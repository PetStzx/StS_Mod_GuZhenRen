package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.YingShengChongPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoJie_YingShengChong extends AbstractTribulation {

    public HaoJie_YingShengChong() {
        super(
                GuZhenRen.makeID("HaoJie_YingShengChong"),
                "应声虫",
                TribulationManager.TRIBULATION_TEXT[2],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new YingShengChongPower(target));
    }
}