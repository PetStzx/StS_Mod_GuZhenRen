package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.YanTongPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YanTongGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("YanTongGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YanTongGu.png");

    private static final int COST = 0;
    private static final int INITIAL_RANK = 4;

    public YanTongGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.YAN_DAO);

        // 基础值为 1
        this.baseMagicNumber = this.magicNumber = 1;

        this.setRank(INITIAL_RANK);
        this.cardsToPreview = new Burn();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 传递 magicNumber 给 Power
        this.addToBot(new ApplyPowerAction(p, p, new YanTongPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();

            // 升级：数值 1 -> 2
            this.upgradeMagicNumber(1);
            this.upgradeRank(1);

            this.initializeDescription();
        }
    }
}