package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.TongXinPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class WanJie_TongXin extends AbstractTribulation {

    public WanJie_TongXin() {
        super(
                GuZhenRen.makeID("WanJie_TongXin"),
                "通心",
                TribulationManager.TRIBULATION_TEXT[3],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new TongXinPower(target));
    }
}