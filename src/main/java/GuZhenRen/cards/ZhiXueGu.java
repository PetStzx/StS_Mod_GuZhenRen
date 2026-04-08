package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.ZhiXuePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhiXueGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ZhiXueGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION; // 引入升级描述
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhiXueGu.png");

    private static final int COST = 0; // 0 费

    private static final int MAGIC = 1; // 给予 1 层止血
    private static final int INITIAL_RANK = 3;

    public ZhiXueGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        this.setDao(Dao.XUE_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 赋予 1 层“止血”
        this.addToBot(new ApplyPowerAction(p, p, new ZhiXuePower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();

            // 升级获得保留属性
            this.selfRetain = true;
            this.myBaseDescription = UPGRADE_DESCRIPTION;

            this.upgradeRank(1); // 3转 -> 4转
            this.initializeDescription();
        }
    }
}