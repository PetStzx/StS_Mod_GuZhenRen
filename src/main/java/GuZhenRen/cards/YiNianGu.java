package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class YiNianGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("YiNianGu");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YiNianGu.png");

    private static final int COST = 1;
    private static final int CARD_AMT = 1;
    private static final int UPGRADE_CARD_AMT = 1;
    private static final int NIAN_AMT = 3;
    private static final int INITIAL_RANK = 4;

    public YiNianGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);
        this.baseMagicNumber = this.magicNumber = CARD_AMT;
        this.baseNian = this.nian = NIAN_AMT;
        this.exhaust = true;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new YiNianAction(this.magicNumber));
        this.addToBot(new ApplyPowerAction(p, p, new NianPower(p, this.nian), this.nian));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_CARD_AMT);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    public static class YiNianAction extends AbstractGameAction {
        private final int amount;

        public YiNianAction(int amount) {
            this.amount = amount;
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            AbstractPlayer p = AbstractDungeon.player;

            if (this.duration == Settings.ACTION_DUR_FAST) {
                if (p.discardPile.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                if (p.discardPile.size() <= this.amount) {
                    ArrayList<AbstractCard> cardsToMove = new ArrayList<>(p.discardPile.group);
                    for (AbstractCard c : cardsToMove) {
                        moveToDeckTop(p, c);
                    }
                    this.isDone = true;
                    return;
                }

                String msg = String.format(YiNianGu.cardStrings.EXTENDED_DESCRIPTION[0], this.amount);
                AbstractDungeon.gridSelectScreen.open(
                        p.discardPile,
                        this.amount,
                        msg,
                        false
                );
                this.tickDuration();
                return;
            }

            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    moveToDeckTop(p, c);
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                p.hand.refreshHandLayout();
            }

            this.tickDuration();
        }

        private void moveToDeckTop(AbstractPlayer p, AbstractCard c) {
            p.discardPile.removeCard(c);
            p.drawPile.addToTop(c);
        }
    }
}