package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.XinXuePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction; // 【新增导包】：回血动作
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XinXue extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("XinXue");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XinXue.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 1;

    public XinXue() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL, // 本命蛊
                CardTarget.SELF);

        this.setDao(Dao.XUE_DAO);

        this.maxRank = 8;

        this.setRank(INITIAL_RANK);
        calculateStats();
    }

    // =========================================================================
    //  核心数值与文本调度器
    // =========================================================================
    private void calculateStats() {
        // 1-8转对应的回血量
        int[] healAmt = {2, 2, 3, 3, 4, 4, 5, 5};

        // 1-8转对应的反伤倍率
        int[] mult = {1, 2, 2, 3, 3, 4, 4, 5};

        this.baseMagicNumber = this.magicNumber = healAmt[this.rank - 1];
        this.baseSecondMagicNumber = this.secondMagicNumber = mult[this.rank - 1];

        if (cardStrings.EXTENDED_DESCRIPTION != null && cardStrings.EXTENDED_DESCRIPTION.length >= 8) {
            this.myBaseDescription = cardStrings.EXTENDED_DESCRIPTION[this.rank - 1];
            this.initializeDescription();
        }
    }

    @Override
    public void applyPowers() {
        calculateStats();
        super.applyPowers();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateStats();

        if (this.magicNumber > 0) {
            this.addToBot(new HealAction(p, p, this.magicNumber));
        }

        this.addToBot(new ApplyPowerAction(p, p, new XinXuePower(p, this.secondMagicNumber), this.secondMagicNumber));
    }

    @Override
    public void performUpgradeEffect() {
        calculateStats();

        this.upgradedMagicNumber = true;
        this.upgradedSecondMagicNumber = true;
    }

    @Override
    protected void onRankLoaded() {
        calculateStats();
    }
}