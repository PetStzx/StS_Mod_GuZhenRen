package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.GuanPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoJie_Guan extends AbstractTribulation {

    public HaoJie_Guan() {
        super(
                GuZhenRen.makeID("HaoJie_Guan"),
                "关",
                TribulationManager.TRIBULATION_TEXT[2],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> {
            int threshold = (int)(target.maxHealth * 0.20f);
            return new GuanPower(target, threshold);
        });
    }
}