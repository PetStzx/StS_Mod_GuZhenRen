package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.HuiJianPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuiJian extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuiJian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION; // 引入升级描述
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuiJian.png");

    private static final int COST = 2; // 始终为 2 费
    private static final int INITIAL_RANK = 7; // 7转起步

    public HuiJian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE, // 金卡
                CardTarget.SELF);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 赋予慧剑能力
        this.addToBot(new ApplyPowerAction(p, p, new HuiJianPower(p)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.selfRetain = true; // 升级后获得保留
            this.upgradeRank(1); // 7转 -> 8转

            this.myBaseDescription = UPGRADE_DESCRIPTION;

            this.initializeDescription();
        }
    }
}