package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.JianDunPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JianDun extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("JianDun");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JianDun.png");

    private static final int COST = 1; // 降为 1 费
    private static final int INITIAL_RANK = 7; // 7转起步

    public JianDun() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE, // 依然保留为金卡（或者你可以自己改为 UNCOMMON 蓝卡）
                CardTarget.SELF);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 由于能力不再叠加，不需要传 amount 参数了
        this.addToBot(new ApplyPowerAction(p, p, new JianDunPower(p)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1); // 7转 -> 8转

            this.isInnate = true; // 升级后固有

            this.myBaseDescription = UPGRADE_DESCRIPTION;

            this.initializeDescription();
        }
    }
}