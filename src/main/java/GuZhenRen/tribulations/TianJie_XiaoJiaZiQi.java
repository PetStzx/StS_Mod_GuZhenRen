package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.XiaoJiaZiQiPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TianJie_XiaoJiaZiQi extends AbstractTribulation {

    public TianJie_XiaoJiaZiQi() {
        super(
                GuZhenRen.makeID("TianJie_XiaoJiaZiQi"),
                "小家子气",
                TribulationManager.TRIBULATION_TEXT[1],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new XiaoJiaZiQiPower(target));
    }
}