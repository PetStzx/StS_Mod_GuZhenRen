package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.SiQiJiangZhiPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TianJie_SiQiJiangZhi extends AbstractTribulation {

    public TianJie_SiQiJiangZhi() {
        super(
                GuZhenRen.makeID("TianJie_SiQiJiangZhi"),
                "死期将至",
                TribulationManager.TRIBULATION_TEXT[1],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new SiQiJiangZhiPower(target, 6));
    }
}