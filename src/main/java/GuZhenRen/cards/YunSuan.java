package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.YunSuanAction;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
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
    private static final int INITIAL_RANK = 6;

    public YunSuan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        this.baseMagicNumber = this.magicNumber = 2;
        this.baseSecondMagicNumber = this.secondMagicNumber = 10;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ExhaustAction(2, false, false, false));
        this.addToBot(new DrawCardAction(p, this.magicNumber));
        this.addToBot(new YunSuanAction(this.secondMagicNumber / 100.0f));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeSecondMagicNumber(5);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}