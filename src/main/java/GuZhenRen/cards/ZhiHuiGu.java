package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.ZhiHuiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhiHuiGu extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("ZhiHuiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhiHuiGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 1;

    public ZhiHuiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL, // 本命蛊
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);
        this.maxRank = 9;

        // 初始化
        this.setRank(INITIAL_RANK);
        calculateStats();
    }

    // 计算当前转数对应的数值
    private void calculateStats() {
        int amount = this.rank + 1;
        if (this.rank >= 9) {
            amount = 9;
            this.isInnate = true;
        } else {
            this.isInnate = false;
        }

        this.baseMagicNumber = this.magicNumber = amount;

        if (this.isInnate && !this.myBaseDescription.contains("固有")) {
            this.myBaseDescription = " 固有 。 " + this.myBaseDescription;
        }
    }

    @Override
    public void applyPowers() {
        calculateStats();
        super.applyPowers();
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateStats();

        // 获得智慧状态
        this.addToBot(new ApplyPowerAction(p, p,
                new ZhiHuiPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void performUpgradeEffect() {
        calculateStats();
        this.upgradedMagicNumber = true;

        if (this.rank == 9) {
            this.initializeDescription();
        }
    }

    @Override
    protected void onRankLoaded() {
        calculateStats();
    }
}