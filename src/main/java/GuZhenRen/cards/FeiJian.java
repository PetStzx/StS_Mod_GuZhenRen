package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.FeiJianAction;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FeiJian extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("FeiJian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/FeiJian.png");

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 2; // 升级后变 8
    private static final int INITIAL_RANK = 6;

    public FeiJian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);
        this.baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new FeiJianAction(m, this.damage));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DMG);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}