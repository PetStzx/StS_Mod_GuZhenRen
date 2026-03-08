package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XingNianGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("XingNianGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XingNianGu.png");

    private static final int COST = 1;
    private static final int SCRY_AMT = 2;
    private static final int UPGRADE_SCRY_AMT = 1;
    private static final int NIAN_GAIN = 3;
    private static final int INITIAL_RANK = 5;

    public XingNianGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        // magicNumber 控制预见数量
        this.baseMagicNumber = this.magicNumber = SCRY_AMT;

        // 【核心修改】启用专属念变量，替代 secondMagicNumber
        this.baseNian = this.nian = NIAN_GAIN;

        this.setRank(INITIAL_RANK);
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new XingNianWrapperAction(this.magicNumber, this.nian));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_SCRY_AMT);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    // =========================================================================
    //  包装动作 (核心逻辑保留，变量名稍微清理)
    // =========================================================================
    public static class XingNianWrapperAction extends AbstractGameAction {
        private final int scryAmount;
        private final int nianPerCard; // 此时这里已经是加成后的值了

        public XingNianWrapperAction(int scryAmount, int nianPerCard) {
            this.scryAmount = scryAmount;
            this.nianPerCard = nianPerCard;
        }

        @Override
        public void update() {
            int startingDiscardSize = AbstractDungeon.player.discardPile.size();

            // 结算逻辑 (放队首，将在预见之后执行)
            AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                @Override
                public void update() {
                    int finalDiscardSize = AbstractDungeon.player.discardPile.size();
                    int discardedCount = finalDiscardSize - startingDiscardSize;

                    if (discardedCount > 0) {
                        for (int i = 0; i < discardedCount; i++) {
                            // 使用传入的 nianPerCard
                            AbstractDungeon.actionManager.addToTop(
                                    new ApplyPowerAction(
                                            AbstractDungeon.player,
                                            AbstractDungeon.player,
                                            new NianPower(AbstractDungeon.player, nianPerCard),
                                            nianPerCard
                                    )
                            );
                        }
                    }
                    this.isDone = true;
                }
            });

            // 原版预见逻辑 (再放队首，将其插在结算前面)
            AbstractDungeon.actionManager.addToTop(new ScryAction(this.scryAmount));

            this.isDone = true;
        }
    }
}