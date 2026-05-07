package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.HeiHuo;
import GuZhenRen.powers.PlayerTribulationPower;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HunDunXiaoNan_HeiHuo extends AbstractTribulation {

    public HunDunXiaoNan_HeiHuo() {
        super(
                GuZhenRen.makeID("HunDunXiaoNan_HeiHuo"),
                "黑火",
                TribulationManager.TRIBULATION_TEXT[4],
                0
        );
    }

    @Override
    public String getDescription() {
        return PlayerTribulationPower.DESCRIPTIONS[7];
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        power.flash();
        int deckSize = AbstractDungeon.player.masterDeck.size();
        int amount = Math.max(1, deckSize / 5);

        AbstractDungeon.actionManager.addToTop(
                new MakeTempCardInDrawPileAction(new HeiHuo(), amount, true, true)
        );
    }
}