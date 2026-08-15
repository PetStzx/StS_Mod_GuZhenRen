package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.ShuiMuTianHuaGuPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DiZai_ShuiMuTianHuaGu extends AbstractTribulation {

    public DiZai_ShuiMuTianHuaGu() {
        super(
                GuZhenRen.makeID("DiZai_ShuiMuTianHuaGu"),
                "水幕天华蛊",
                TribulationManager.TRIBULATION_TEXT[0], // 地灾
                2 // 增强敌人
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> {
            int amount = 25;
            if (target != null && target.hasPower("Barricade")) {
                amount = 20; // 目标有壁垒时降低至 20 层
            }
            return new ShuiMuTianHuaGuPower(target, amount);
        });
    }
}