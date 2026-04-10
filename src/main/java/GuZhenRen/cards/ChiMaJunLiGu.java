package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChiMaJunLiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ChiMaJunLiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ChiMaJunLiGu.png");

    private static final int COST = 1;
    private static final int DRAW = 2;
    private static final int UPGRADE_DRAW = 1; // 升级后+1，共3
    private static final int INITIAL_RANK = 2;

    public ChiMaJunLiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
        this.baseMagicNumber = this.magicNumber = DRAW;
        this.exhaust = true;

        this.cardsToPreview = new MaLiXuYing();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 抽牌
        this.addToBot(new DrawCardAction(p, this.magicNumber));

        // 2. 生成马力虚影
        AbstractCard c = this.cardsToPreview.makeStatEquivalentCopy();
        this.addToBot(new MakeTempCardInHandAction(c, 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_DRAW); // 2 -> 3
            this.upgradeRank(1); // 2转 -> 3转
            this.cardsToPreview.upgrade();
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}