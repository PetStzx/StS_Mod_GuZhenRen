package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.QuanLiYiFuPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class QuanLiYiFuGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("QuanLiYiFuGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/QuanLiYiFuGu.png");

    private static final int COST = 3;
    private static final int INITIAL_RANK = 5; // 初始5转

    public QuanLiYiFuGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!p.hasPower(QuanLiYiFuPower.POWER_ID)) {
            this.addToBot(new ApplyPowerAction(p, p, new QuanLiYiFuPower(p,-1)));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.selfRetain = true; // 升级后保留
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.upgradeRank(1); // 5转 -> 6转
            this.initializeDescription();
        }
    }
}