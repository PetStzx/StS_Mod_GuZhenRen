package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.TunHuoPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TunHuoGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("TunHuoGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/TunHuoGu.png");

    private static final int COST = 2;
    private static final int UPGRADE_COST = 1;
    private static final int INITIAL_RANK = 4;
    private static final int BURN_AMT = 1; // 基础给予1层焚烧

    public TunHuoGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.YAN_DAO);

        // 魔法值：焚烧层数
        this.baseMagicNumber = this.magicNumber = BURN_AMT;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得吞火能力
        this.addToBot(new ApplyPowerAction(p, p,
                new TunHuoPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(UPGRADE_COST); // 2 -> 1
            this.upgradeRank(1); // 4 -> 5
            this.initializeDescription();
        }
    }
}