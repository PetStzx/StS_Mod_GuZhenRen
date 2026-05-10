package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.HuanManPower;
import GuZhenRen.powers.PlayerTribulationPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TianJie_HuanMan extends AbstractTribulation {

    public TianJie_HuanMan() {
        super(
                GuZhenRen.makeID("TianJie_HuanMan"),
                "缓慢",
                TribulationManager.TRIBULATION_TEXT[1],
                0
        );
    }

    @Override
    public String getDescription() {
        return PlayerTribulationPower.DESCRIPTIONS[4];
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        power.flash();
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(
                        AbstractDungeon.player,
                        AbstractDungeon.player,
                        new HuanManPower(AbstractDungeon.player, 0),
                        0
                )
        );
    }
}