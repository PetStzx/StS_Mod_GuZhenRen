package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.SongZhenPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoJie_SongZhen extends AbstractTribulation {

    public HaoJie_SongZhen() {
        super(
                GuZhenRen.makeID("HaoJie_SongZhen"),
                "松针",
                TribulationManager.TRIBULATION_TEXT[2],
                2
        );
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        applyPowerToRandomEnemyAction(target -> new SongZhenPower(target, 2));
    }
}