package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.TouShengAction;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TouSheng extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("TouSheng");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/TouSheng.png");

    private static final int COST = 3;
    private static final int MAGIC = 1; // 默认偷 1 点
    private static final int UPGRADE_PLUS_MAGIC = 1; // 升级后偷 2 点

    private static final int INITIAL_RANK = 8; // 8转仙蛊

    public TouSheng() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE, // 金卡
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.TOU_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.setRank(INITIAL_RANK);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new TouShengAction(this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1); // 8转 -> 9转
            this.initializeDescription();
        }
    }
}