package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import GuZhenRen.powers.YanDaoDaoHenPower;
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
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
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

        // 1. 第一魔法值：用于存储转数/消耗上限
        this.baseMagicNumber = this.magicNumber = 1;

        // 2. 第二魔法值：用于存储焚烧层数 (基础为1)
        this.baseSecondMagicNumber = this.secondMagicNumber = 1;

        this.maxRank = 9;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void applyPowers() {
        // 1. 重置数值
        this.baseMagicNumber = this.rank;
        this.magicNumber = this.baseMagicNumber;
        this.secondMagicNumber = this.baseSecondMagicNumber; // 先重置回基础值

        super.applyPowers();

        // =====================================================================
        // 【核心修复】 必须先检查 player 是否为空！
        // 因为在主菜单图鉴界面，AbstractDungeon.player 是 null。
        // =====================================================================
        if (AbstractDungeon.player == null) {
            this.initializeDescription();
            return;
        }

        // 2. 【显示逻辑】计算炎道道痕加成
        int bonus = 0;
        if (AbstractDungeon.player.hasPower(YanDaoDaoHenPower.POWER_ID)) {
            bonus = AbstractDungeon.player.getPower(YanDaoDaoHenPower.POWER_ID).amount / 2;
        }

        if (bonus > 0) {
            this.secondMagicNumber += bonus;
            this.isSecondMagicNumberModified = true;
        }

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int maxExhaust = this.rank;

        // 【生效逻辑】 传递 baseSecondMagicNumber (1)
        this.addToBot(new HuoGuAction(maxExhaust, this.baseSecondMagicNumber, m));
    }

    @Override
    public void performUpgradeEffect() {
        this.upgradedMagicNumber = true;
    }

    public static class HuoGuAction extends AbstractGameAction {
        private final AbstractMonster target;
        private final int maxAmount;
        private final int burnAmount; // 焚烧层数

        public HuoGuAction(int maxAmount, int burnAmount, AbstractMonster target) {
            this.target = target;
            this.maxAmount = maxAmount;
            this.burnAmount = burnAmount;
            this.actionType = ActionType.WAIT;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        public HuoGuAction(int maxAmount, AbstractMonster target) {
            this(maxAmount, 1, target);
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                if (AbstractDungeon.player.hand.isEmpty()) {
                    this.isDone = true;
                    return;
                }
                AbstractDungeon.handCardSelectScreen.open(
                        "消耗至多 " + maxAmount + " 张手牌",
                        maxAmount,
                        true,
                        true
                );
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
                        // 使用传入的基础值 (burnAmount)
                        this.addToBot(new ApplyPowerAction(target, AbstractDungeon.player,
                                new FenShaoPower(target, burnAmount), burnAmount, true));
                    }
                }
            }
            this.isDone = true;
        }
    }
}