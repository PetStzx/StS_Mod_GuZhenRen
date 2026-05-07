package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.CaoMangPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TianJie_CaoMang extends AbstractTribulation {

    public TianJie_CaoMang() {
        super(
                GuZhenRen.makeID("TianJie_CaoMang"),
                "草莽",
                TribulationManager.TRIBULATION_TEXT[1],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new CaoMangPower(target, 7));
    }
}