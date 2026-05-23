package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.HuaShaPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuangSha extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuangSha");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuangSha.png");

    private static final int COST = 3;
    private static final int UPGRADE_COST = 2;
    private static final int INITIAL_RANK = 6;

    public HuangSha() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);

        this.setDao(Dao.TU_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new HuaShaPower(p)));
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