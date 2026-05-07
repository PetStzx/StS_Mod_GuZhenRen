package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.PlayerTribulationPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoJie_XuKong extends AbstractTribulation {

    public HaoJie_XuKong() {
        super(
                GuZhenRen.makeID("HaoJie_XuKong"),
                "虚空",
                TribulationManager.TRIBULATION_TEXT[2],
                0
        );
    }

    @Override
    public String getDescription() {
        return PlayerTribulationPower.DESCRIPTIONS[5];
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        power.flash();
        AbstractDungeon.actionManager.addToTop(new MakeTempCardInDiscardAction(new VoidCard(), 1));
    }

    @Override
    public void atStartOfTurn(AbstractPower power) {
        power.flash();
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new VoidCard(), 1));
    }
}