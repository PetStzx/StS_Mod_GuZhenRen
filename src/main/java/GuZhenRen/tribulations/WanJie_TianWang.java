package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.TianWangPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class WanJie_TianWang extends AbstractTribulation {

    public WanJie_TianWang() {
        super(
                GuZhenRen.makeID("WanJie_TianWang"),
                "天网",
                TribulationManager.TRIBULATION_TEXT[3],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new TianWangPower(target));
    }
}