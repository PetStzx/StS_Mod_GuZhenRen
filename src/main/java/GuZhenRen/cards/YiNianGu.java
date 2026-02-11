package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import GuZhenRen.powers.QingPower; // 导入情
import GuZhenRen.powers.ZhiDaoDaoHenPower; // 导入智道道痕
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
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
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

        // magicNumber 控制选牌数
        this.baseMagicNumber = this.magicNumber = CARD_AMT;
        // secondMagicNumber 控制念获取数
        this.baseSecondMagicNumber = this.secondMagicNumber = NIAN_AMT;

        // 加上消耗，防止无限循环
        this.exhaust = true;

        this.setRank(INITIAL_RANK);
    }

    // =========================================================================
    //  新增 applyPowers：用于动态计算显示的念数值
    // =========================================================================
    @Override
    public void applyPowers() {
        // 1. 先重置数值
        this.secondMagicNumber = this.baseSecondMagicNumber;

        // 2. 调用父类逻辑
        super.applyPowers();

        // 3. 计算加成
        int bonus = 0;

        // 计算【情】的加成
        if (AbstractDungeon.player.hasPower(QingPower.POWER_ID)) {
            // 每3层+1
            bonus += AbstractDungeon.player.getPower(QingPower.POWER_ID).amount / 3;
        }

        // 计算【智道道痕】的加成
        if (AbstractDungeon.player.hasPower(ZhiDaoDaoHenPower.POWER_ID)) {
            // 每3层+1
            bonus += AbstractDungeon.player.getPower(ZhiDaoDaoHenPower.POWER_ID).amount / 3;
        }

        // 4. 应用加成
        if (bonus > 0) {
            this.secondMagicNumber += bonus;
            this.isSecondMagicNumberModified = true;
        }

        // 刷新描述文本
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 选牌并放到牌堆顶
        this.addToBot(new YiNianAction(this.magicNumber));

        // 2. 获得念
        // 注意：这里传入 baseSecondMagicNumber (基础值)
        // NianPower 的构造函数会自动再次计算 bonus，所以实际获得的层数是正确的 (基础+加成)
        this.addToBot(new ApplyPowerAction(p, p,
                new NianPower(p, this.baseSecondMagicNumber), this.baseSecondMagicNumber));
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

    // =========================================================================
    //  Action 类 (保持修复后的版本)
    // =========================================================================
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

                // 如果弃牌堆数量 <= 需要选择的数量，直接全部拿回来
                if (p.discardPile.size() <= this.amount) {
                    ArrayList<AbstractCard> cardsToMove = new ArrayList<>(p.discardPile.group);
                    for (AbstractCard c : cardsToMove) {
                        moveToDeckTop(p, c);
                    }
                    this.isDone = true;
                    return;
                }

                AbstractDungeon.gridSelectScreen.open(
                        p.discardPile,
                        this.amount,
                        "选择" + this.amount + "张牌放到抽牌堆顶部",
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