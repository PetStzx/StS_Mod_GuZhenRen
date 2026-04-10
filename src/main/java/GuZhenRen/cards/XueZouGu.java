package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;

public class XueZouGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("XueZouGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueZouGu.png");

    private static final int COST = 0;

    private static final int HP_LOSS = 2;

    // 当回合抽牌数
    private static final int DRAW_AMT = 2;

    private static final int INITIAL_RANK = 1;

    public XueZouGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        this.setDao(Dao.XUE_DAO);

        this.baseMagicNumber = this.magicNumber = HP_LOSS;
        this.baseSecondMagicNumber = this.secondMagicNumber = DRAW_AMT;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 失去生命
        this.addToBot(new LoseHPAction(p, p, this.magicNumber));

        // 2. 抽牌
        this.addToBot(new DrawCardAction(p, this.secondMagicNumber));

        // 3. 升级后附加：下回合开始时抽 1 张牌
        if (this.upgraded) {
            this.addToBot(new ApplyPowerAction(p, p, new DrawCardNextTurnPower(p, 1), 1));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.upgradeRank(1); // 1转 -> 2转
            this.initializeDescription();
        }
    }
}