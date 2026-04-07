package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuoGu extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("HuoGu");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuoGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 1;

    public HuoGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.ENEMY);

        this.setDao(Dao.YAN_DAO);
        this.isEthereal = true;
        this.maxRank = 9;

        this.baseFenShao = this.fenShao = 1;

        this.setRank(INITIAL_RANK);
        calculateStats();
    }

    private void calculateStats() {
        this.baseMagicNumber = this.magicNumber = this.rank;
        this.myBaseDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void applyPowers() {
        calculateStats();
        super.applyPowers();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateStats();
        this.addToBot(new HuoGuAction(this.magicNumber, this.fenShao, m));
    }

    @Override
    public void performUpgradeEffect() {
        calculateStats();
        this.upgradedMagicNumber = true;
    }

    @Override
    protected void onRankLoaded() {
        calculateStats();
    }

    public static class HuoGuAction extends AbstractGameAction {
        private final AbstractMonster target;
        private final int maxAmount;
        private final int burnAmount;

        public HuoGuAction(int maxAmount, int burnAmount, AbstractMonster target) {
            this.target = target;
            this.maxAmount = maxAmount;
            this.burnAmount = burnAmount;
            this.actionType = ActionType.WAIT;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                if (AbstractDungeon.player.hand.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                String msg = String.format(HuoGu.cardStrings.EXTENDED_DESCRIPTION[0], maxAmount);
                AbstractDungeon.handCardSelectScreen.open(msg, maxAmount, true, true);
                this.tickDuration();
                return;
            }

            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                int count = 0;

                for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                    AbstractDungeon.player.hand.moveToExhaustPile(c);
                    count++;
                }

                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();

                if (count > 0 && target != null && !target.isDeadOrEscaped()) {
                    for (int i = 0; i < count; i++) {
                        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(target, AbstractDungeon.player,
                                new FenShaoPower(target, burnAmount), burnAmount, true));
                    }
                }
            }
            this.isDone = true;
        }
    }
}