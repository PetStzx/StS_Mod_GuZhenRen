package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.DouZhuanPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class WanJie_DouZhuan extends AbstractTribulation {

    public WanJie_DouZhuan() {
        super(
                GuZhenRen.makeID("WanJie_DouZhuan"),
                "斗转",
                TribulationManager.TRIBULATION_TEXT[3],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new DouZhuanPower(target));
    }
}