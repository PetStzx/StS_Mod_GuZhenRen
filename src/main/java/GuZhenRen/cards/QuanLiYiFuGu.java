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
    // 暂时使用占位图
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/QuanLiYiFuGu.png");

    private static final int COST = 3;
    private static final int UPGRADE_COST = 2;
    private static final int INITIAL_RANK = 3; // 初始3转

    public QuanLiYiFuGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 罕见
                CardTarget.SELF);

        // 流派：力道
        this.setDao(Dao.LI_DAO);


        // 3转
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 核心逻辑：类似原版"腐化"，如果不叠加，就先判断有没有
        if (!p.hasPower(QuanLiYiFuPower.POWER_ID)) {
            this.addToBot(new ApplyPowerAction(p, p, new QuanLiYiFuPower(p)));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(UPGRADE_COST); // 3 -> 2
            this.upgradeRank(1); // 3转 -> 4转
            this.initializeDescription();
        }
    }
}