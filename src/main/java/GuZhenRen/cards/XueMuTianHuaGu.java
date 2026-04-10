package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.XueMuTianHuaPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XueMuTianHuaGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("XueMuTianHuaGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueMuTianHuaGu.png");

    private static final int COST = 2;
    private static final int UPGRADE_COST = 1; // 升级后 1 费
    private static final int MAGIC = 1; //  1 层缓冲
    private static final int INITIAL_RANK = 5;

    public XueMuTianHuaGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);

        this.setDao(Dao.XUE_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new XueMuTianHuaPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(UPGRADE_COST); // 2 -> 1 费
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}