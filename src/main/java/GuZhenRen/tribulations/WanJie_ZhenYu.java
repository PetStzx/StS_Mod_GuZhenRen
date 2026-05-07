package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.ZhenYuPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class WanJie_ZhenYu extends AbstractTribulation {

    public WanJie_ZhenYu() {
        super(
                GuZhenRen.makeID("WanJie_ZhenYu"),
                "镇宇",
                TribulationManager.TRIBULATION_TEXT[3],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new ZhenYuPower(target));
    }
}