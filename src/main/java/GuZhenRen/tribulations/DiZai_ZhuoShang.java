package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.PlayerTribulationPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DiZai_ZhuoShang extends AbstractTribulation {

    public DiZai_ZhuoShang() {
        super(
                GuZhenRen.makeID("DiZai_ZhuoShang"),
                "灼伤",
                TribulationManager.TRIBULATION_TEXT[0],
                0
        );
    }

    @Override
    public String getDescription() {
        return PlayerTribulationPower.DESCRIPTIONS[3];
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        power.flash();
        AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(new Burn(), 1, false));
    }

    @Override
    public void atStartOfTurn(AbstractPower power) {
        power.flash();
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Burn(), 1, false));
    }
}