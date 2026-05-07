package GuZhenRen.tribulations;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.HunDun;
import GuZhenRen.powers.PlayerTribulationPower;
import GuZhenRen.relics.AbstractKongQiao;
import GuZhenRen.util.TribulationManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HunDunDaNan_HunDun extends AbstractTribulation {

    public HunDunDaNan_HunDun() {
        super(
                GuZhenRen.makeID("HunDunDaNan_HunDun"),
                "混沌",
                TribulationManager.TRIBULATION_TEXT[5],
                0
        );
    }

    private int getDivisor() {
        int count = 1;
        if (AbstractDungeon.player != null) {
            AbstractKongQiao relic = AbstractKongQiao.getInstance();
            if (relic != null) {
                String targetType = TribulationManager.TRIBULATION_TEXT[5];
                for (int i = 0; i < TribulationManager.TRIBULATION_TEXT.length; i++) {
                    if (TribulationManager.TRIBULATION_TEXT[i].equals(targetType)) {
                        count = Math.max(1, relic.drawCounts[i]);
                        break;
                    }
                }
            }
        }

        if (count == 1) return 8;
        if (count == 2) return 5;
        return 2;
    }

    @Override
    public String getDescription() {
        return String.format(PlayerTribulationPower.DESCRIPTIONS[8], getDivisor());
    }

    @Override
    public void atPreBattle(AbstractPower power) {
        power.flash();

        int deckSize = AbstractDungeon.player.masterDeck.size();
        int divisor = getDivisor();
        int amount = Math.max(1, deckSize / divisor);

        AbstractDungeon.actionManager.addToTop(
                new MakeTempCardInDrawPileAction(new HunDun(), amount, true, true)
        );
    }
}