package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LiLiangGu extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("LiLiangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LiLiangGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 1;

    public LiLiangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL, // 本命蛊为Special
                CardTarget.SELF);

        this.setDao(Dao.LI_DAO);

        this.exhaust = true; // 消耗
        this.maxRank = 9;

        // 使用第二魔法值来代替 damage
        this.baseMagicNumber = this.magicNumber = 2; // 获得力量
        this.baseSecondMagicNumber = this.secondMagicNumber = 1; // 失去力量

        this.setRank(INITIAL_RANK);
    }

    // 计算当前转数对应的数值
    private void calculateStats() {
        // 公式推导：
        // 获得力量 = (转数 / 2) + 2。 (1转->2, 2转->3, 3转->3, ..., 8转->6, 9转->6)
        // 失去力量 = 偶数转失去2点，奇数转失去1点。

        int gainAmount = (this.rank / 2) + 2;
        int loseAmount = (this.rank % 2 == 0) ? 2 : 1;

        this.baseMagicNumber = this.magicNumber = gainAmount;
        // 将计算结果赋给第二魔法值
        this.baseSecondMagicNumber = this.secondMagicNumber = loseAmount;
    }

    @Override
    public void applyPowers() {
        calculateStats();
        super.applyPowers();
        // 刷新描述
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 确保数值是最新的
        calculateStats();

        // 1. 获得力量
        this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));

        // 2. 回合结束失去力量 (读取 secondMagicNumber)
        this.addToBot(new ApplyPowerAction(p, p, new LoseStrengthPower(p, this.secondMagicNumber), this.secondMagicNumber));
    }

    @Override
    public void performUpgradeEffect() {
        // 升级后重新计算数值
        calculateStats();

        this.upgradedMagicNumber = true;       // 魔法值显示为绿色
        this.upgradedSecondMagicNumber = true; // 第二魔法值显示为绿色
    }
    @Override
    protected void onRankLoaded() {
        calculateStats();
    }
}