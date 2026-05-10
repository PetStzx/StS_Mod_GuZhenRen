package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.GuiGuaYiPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoJie_GuiGuaYi extends AbstractTribulation {

    public HaoJie_GuiGuaYi() {
        super(
                GuZhenRen.makeID("HaoJie_GuiGuaYi"),
                "鬼卦衣",
                TribulationManager.TRIBULATION_TEXT[2],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToAllEnemiesAction(target -> new GuiGuaYiPower(target));
    }
}