package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WuZuNiao extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("WuZuNiao");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WuZuNiao.png");

    private static final int COST = 1;
    private static final int DRAW_AMT = 3;
    private static final int UPGRADE_DRAW_AMT = 1; // 升级后增加抽1张，即变成4张
    private static final int INITIAL_RANK = 3; // 初始3转

    public WuZuNiao() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        // 流派：骨道
        this.setDao(Dao.GU_DAO);


        // 核心数值：抽牌数
        this.baseMagicNumber = this.magicNumber = DRAW_AMT;

        // 核心机制：虚无
        this.isEthereal = true;

        // 初始转数
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 抽牌动作
        this.addToBot(new DrawCardAction(p, this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            // 抽牌数 3 -> 4
            this.upgradeMagicNumber(UPGRADE_DRAW_AMT);
            // 转数 3 -> 4
            this.upgradeRank(1);

            this.initializeDescription();
        }
    }
}