package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.ZhenPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoJie_Zhen extends AbstractTribulation {

    public HaoJie_Zhen() {
        super(
                GuZhenRen.makeID("HaoJie_Zhen"),
                "镇",
                TribulationManager.TRIBULATION_TEXT[2],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new ZhenPower(target));
    }
}