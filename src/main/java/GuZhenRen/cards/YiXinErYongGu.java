package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.YiXinErYongPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YiXinErYongGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("YiXinErYongGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YiXinErYongGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 2; // 2转
    private static final int POWER_AMT = 1;
    private static final int UPGRADE_POWER_AMT = 1; // 升级后变为2次

    public YiXinErYongGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON, // 白卡
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        // 这里的 MagicNumber 代表触发次数
        this.baseMagicNumber = this.magicNumber = POWER_AMT;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p,
                new YiXinErYongPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            // 升级后：下 2 次打出牌时生效
            this.upgradeMagicNumber(UPGRADE_POWER_AMT);
            this.upgradeRank(1); // 2转 -> 3转
            this.initializeDescription();
        }
    }
}