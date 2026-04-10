package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.ZhiHuiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhiHuiGu extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("ZhiHuiGu");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhiHuiGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 1;

    public ZhiHuiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);
        this.maxRank = 9;

        this.setRank(INITIAL_RANK);
        calculateStats();
    }

    private void calculateStats() {
        int amount = this.rank + 1;
        if (this.rank >= 9) {
            amount = 9;
            this.isInnate = true;
        } else {
            this.isInnate = false;
        }

        this.baseMagicNumber = this.magicNumber = amount;

        String innatePrefix = ZhiHuiGu.cardStrings.EXTENDED_DESCRIPTION[0]; // " 固有 。 "
        String innateCheck = ZhiHuiGu.cardStrings.EXTENDED_DESCRIPTION[1];  // "固有"

        if (this.isInnate && !this.myBaseDescription.contains(innateCheck)) {
            this.myBaseDescription = innatePrefix + this.myBaseDescription;
        }
    }

    @Override
    public void applyPowers() {
        calculateStats();
        super.applyPowers();
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateStats();

        this.addToBot(new ApplyPowerAction(p, p,
                new ZhiHuiPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void performUpgradeEffect() {
        calculateStats();
        this.upgradedMagicNumber = true;

        if (this.rank == 9) {
            this.initializeDescription();
        }
    }

    @Override
    protected void onRankLoaded() {
        calculateStats();
    }
}