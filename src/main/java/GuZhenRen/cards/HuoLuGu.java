package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuoLuGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuoLuGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuoLuGu.png");

    private static final int COST = 0;
    private static final int CARD_AMT = 2;
    private static final int UPGRADE_CARD_AMT = 1;
    private static final int INITIAL_RANK = 2;

    public HuoLuGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.NONE);

        this.setDao(Dao.YAN_DAO);


        this.baseMagicNumber = this.magicNumber = CARD_AMT;

        this.setRank(INITIAL_RANK);

        // 预览火炭蛊 (默认为未升级状态)
        this.cardsToPreview = new HuoTanGu();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 不需要传入升级状态了
        this.addToBot(new HuoLuGuAction(1, this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_CARD_AMT);
            this.upgradeRank(1);

            // 【修改】移除了 this.cardsToPreview.upgrade();
            // 这样升级火炉蛊后，预览的火炭蛊依然是1转的

            this.initializeDescription();
        }
    }

    public static class HuoLuGuAction extends AbstractGameAction {
        private final int exhaustAmount;
        private final int addAmount;

        // 【修改】移除了 upgradeResult 参数
        public HuoLuGuAction(int exhaustAmount, int addAmount) {
            this.exhaustAmount = exhaustAmount;
            this.addAmount = addAmount;
            this.duration = Settings.ACTION_DUR_FAST;
            this.actionType = ActionType.EXHAUST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                if (AbstractDungeon.player.hand.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                AbstractDungeon.handCardSelectScreen.open(
                        "消耗",
                        this.exhaustAmount,
                        false,
                        false
                );
                this.tickDuration();
                return;
            }

            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                    AbstractDungeon.player.hand.moveToExhaustPile(c);
                }

                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();

                // 始终生成未升级的火炭蛊
                AbstractCard c = new HuoTanGu();

                this.addToTop(new MakeTempCardInHandAction(c, this.addAmount));
            }

            this.isDone = true;
        }
    }
}