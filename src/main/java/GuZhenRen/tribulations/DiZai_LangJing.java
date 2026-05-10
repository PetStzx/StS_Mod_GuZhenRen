package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.LangJingPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DiZai_LangJing extends AbstractTribulation {

    public DiZai_LangJing() {
        super(
                GuZhenRen.makeID("DiZai_LangJing"),
                "浪静",
                TribulationManager.TRIBULATION_TEXT[0], // 地灾
                2 // 增强敌人
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new LangJingPower(target, 0));
    }
}