package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.IProbabilityModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class YunDaoDaoHenPower extends AbstractDaoHenPower implements IProbabilityModifier {
    public static final String POWER_ID = GuZhenRen.makeID("YunDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public YunDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        int totalBonus = this.amount * 3;
        this.description = powerStrings.DESCRIPTIONS[0] + totalBonus + powerStrings.DESCRIPTIONS[1];
    }


    @Override
    public float getAdditiveProbability(AbstractCard card) {

        return 0.03f * this.amount;
    }


    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        refreshHandDescriptions();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        refreshHandDescriptions();
    }

    @Override
    public void onRemove() {
        super.onRemove();
        refreshHandDescriptions();
    }

    private void refreshHandDescriptions() {
        if (AbstractDungeon.player != null && AbstractDungeon.player.hand != null) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                c.initializeDescription();
            }
        }
    }
}