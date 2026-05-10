package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.ZhengChangPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TianJie_ZhengChang extends AbstractTribulation {

    public TianJie_ZhengChang() {
        super(
                GuZhenRen.makeID("TianJie_ZhengChang"),
                "正常",
                TribulationManager.TRIBULATION_TEXT[1], // 天劫
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new ZhengChangPower(target, 3));
    }
}