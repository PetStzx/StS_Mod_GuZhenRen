package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FeiXingPower;
import GuZhenRen.powers.ZhenChiGaoFeiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WuZuNiao extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("WuZuNiao");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WuZuNiao.png");

    private static final int COST = 2;
    private static final int UPGRADE_COST = 1;
    private static final int BLOCK_GAIN = 3;
    private static final int INITIAL_RANK = 3;

    public WuZuNiao() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.GU_DAO);
        this.setRank(INITIAL_RANK);
        this.baseMagicNumber = this.magicNumber = BLOCK_GAIN;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new FeiXingPower(p, 1), 1));
        this.addToBot(new ApplyPowerAction(p, p, new ZhenChiGaoFeiPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(UPGRADE_COST);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}