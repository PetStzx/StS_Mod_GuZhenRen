package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.ShanYaoPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GuangGu extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("GuangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/GuangGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 1;

    public GuangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.SELF);

        this.setDao(Dao.GUANG_DAO);
        this.maxRank = 9;

        // 初始化
        this.setRank(INITIAL_RANK);
        calculateStats();
    }

    // 根据当前转数动态计算倍率百分比
    private void calculateStats() {
        // 公式: (转数 + 1) * 50
        // 1转 = 100% (2层)
        // 9转 = 500% (10层)
        int percentage = (this.rank + 1) * 50;

        this.baseMagicNumber = this.magicNumber = percentage;
    }

    @Override
    public void applyPowers() {
        calculateStats();
        super.applyPowers();
        this.initializeDescription(); // 确保描述中的 !M! 实时刷新
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateStats();
        int powerStacks = this.magicNumber / 50;
        // 赋予自身闪耀层数
        this.addToBot(new ApplyPowerAction(p, p, new ShanYaoPower(p, powerStacks), powerStacks));
    }

    @Override
    public void performUpgradeEffect() {
        calculateStats();
        this.upgradedMagicNumber = true;
    }

    @Override
    protected void onRankLoaded() {
        calculateStats(); // 读档时重新计算数值
    }
}