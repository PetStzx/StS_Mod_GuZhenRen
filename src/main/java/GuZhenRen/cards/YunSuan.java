package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.YunSuanAction;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YunSuan extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("YunSuan");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YunSuan.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 6; // 6转

    public YunSuan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        this.baseMagicNumber = this.magicNumber = 2; // 抽2张牌

        // 使用 SecondMagicNumber 来控制概率提升幅度（15表示15%）
        this.baseSecondMagicNumber = this.secondMagicNumber = 15;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 传入抽牌数和要增加的概率（转化为小数 0.15f）
        this.addToBot(new YunSuanAction(this.magicNumber, this.secondMagicNumber / 100.0f));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1); // 抽牌数 2 -> 3
            this.upgradeRank(1);        // 6转 -> 7转
            this.initializeDescription();
        }
    }
}