package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import GuZhenRen.powers.QingPower;
import GuZhenRen.powers.ZhiDaoDaoHenPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction; // 导入原版预见
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
    private static final int SCRY_AMT = 3;
    private static final int UPGRADE_SCRY_AMT = 2;
    private static final int NIAN_GAIN = 3;
    private static final int INITIAL_RANK = 5;

    public XingNianGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);
        this.baseMagicNumber = this.magicNumber = SCRY_AMT;
        this.baseSecondMagicNumber = this.secondMagicNumber = NIAN_GAIN;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void applyPowers() {
        this.secondMagicNumber = this.baseSecondMagicNumber;
        super.applyPowers();

        int bonus = 0;
        if (AbstractDungeon.player.hasPower(QingPower.POWER_ID)) {
            bonus += AbstractDungeon.player.getPower(QingPower.POWER_ID).amount / 3;
        }
        if (AbstractDungeon.player.hasPower(ZhiDaoDaoHenPower.POWER_ID)) {
            bonus += AbstractDungeon.player.getPower(ZhiDaoDaoHenPower.POWER_ID).amount / 3;
        }

        if (bonus > 0) {
            this.secondMagicNumber += bonus;
            this.isSecondMagicNumberModified = true;
        }
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 使用自定义的包装动作，来“监控”原版预见的结果
        this.addToBot(new XingNianWrapperAction(this.magicNumber, this.baseSecondMagicNumber));
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
    //  包装动作：用于计算预见丢弃了多少张牌
    // =========================================================================
    public static class XingNianWrapperAction extends AbstractGameAction {
        private final int scryAmount;
        private final int nianPerCard;

        public XingNianWrapperAction(int scryAmount, int nianPerCard) {
            this.scryAmount = scryAmount;
            this.nianPerCard = nianPerCard;
        }

        @Override
        public void update() {
            // 1. 记录动作开始前，弃牌堆里有几张牌
            int startingDiscardSize = AbstractDungeon.player.discardPile.size();

            // 2. 将“结算与发奖逻辑”添加到队列顶部 (Top)
            // 注意：addToTop 是后进先出。所以我们要先加“结算”，再加“预见”。
            // 这样执行顺序才是：[预见] -> [结算]

            // Step B: 结算逻辑 (后加的先执行? 不，addToTop是插队。
            // 队列当前：[Wrapper(正在执行)]
            // 我们需要最终顺序：Wrapper结束 -> ScryAction -> CheckDiffAction

            // 正确写法：
            // 先把 CheckDiffAction 插到队首
            AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                @Override
                public void update() {
                    // 再次检查弃牌堆数量
                    int finalDiscardSize = AbstractDungeon.player.discardPile.size();
                    // 计算差值 = 刚才预见丢弃的数量
                    int discardedCount = finalDiscardSize - startingDiscardSize;

                    if (discardedCount > 0) {
                        // 循环给予念，以触发多次加成
                        for (int i = 0; i < discardedCount; i++) {
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

            // Step A: 原版预见逻辑 (再插到队首，排在CheckDiff前面)
            // 这样 ScryAction 会先执行，改变 discardPile 的数量
            // 原版 ScryAction 会自动处理 GoldenEye，不需要我们操心
            AbstractDungeon.actionManager.addToTop(new ScryAction(this.scryAmount));

            this.isDone = true;
        }
    }
}