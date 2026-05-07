package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.ZiAiPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DiZai_ZiAi extends AbstractTribulation {

    public DiZai_ZiAi() {
        super(
                GuZhenRen.makeID("DiZai_ZiAi"),
                "自爱",
                TribulationManager.TRIBULATION_TEXT[0],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new ZiAiPower(target));
    }
}