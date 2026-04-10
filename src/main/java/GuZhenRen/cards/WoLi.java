package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DoubleTapPower;

public class WoLi extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("WoLi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WoLi.png");

    private static final int COST = 2;
    private static final int INITIAL_RANK = 6;

    public WoLi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
        this.exhaust = true;

        this.cardsToPreview = new WoLiXuYing();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得 1 层“双发”
        this.addToBot(new ApplyPowerAction(p, p, new DoubleTapPower(p, 1), 1));

        // 2. 生成我力虚影
        AbstractCard c = this.cardsToPreview.makeStatEquivalentCopy();
        this.addToBot(new MakeTempCardInHandAction(c, 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1); // 6转 -> 7转
            this.cardsToPreview.upgrade();
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}