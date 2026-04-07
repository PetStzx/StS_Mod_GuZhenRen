package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.BianHuaDaoDaoHenPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BianXing extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("BianXing");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/BianXing.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 1;

    public BianXing() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.SELF);

        this.setDao(Dao.BIAN_HUA_DAO);
        this.maxRank = 9;

        this.setRank(INITIAL_RANK);
        calculateStats();
    }

    private void calculateStats() {
        this.baseMagicNumber = this.magicNumber = this.rank;
        this.myBaseDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    @Override
    public void applyPowers() {
        calculateStats();
        super.applyPowers();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateStats();
        this.addToBot(new ApplyPowerAction(p, p, new BianHuaDaoDaoHenPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void performUpgradeEffect() {
        calculateStats();
        this.upgradedMagicNumber = true;
    }

    @Override
    protected void onRankLoaded() {
        calculateStats();
    }
}