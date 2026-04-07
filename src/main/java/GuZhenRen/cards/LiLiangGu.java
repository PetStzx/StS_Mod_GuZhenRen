package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LiLiangGu extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("LiLiangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LiLiangGu.png");

    private static final int COST = 0;
    private static final int INITIAL_RANK = 1;

    public LiLiangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.SELF);

        this.setDao(Dao.LI_DAO);
        this.exhaust = true;
        this.maxRank = 9;

        this.setRank(INITIAL_RANK);
        calculateStats();
    }

    private void calculateStats() {
        int[] gains = {0, 2, 2, 3, 3, 4, 4, 5, 6, 7};
        int[] loses = {0, 1, 0, 1, 0, 1, 0, 0, 0, 0};

        int rankIndex = Math.min(Math.max(this.rank, 1), 9);

        this.baseMagicNumber = this.magicNumber = gains[rankIndex];
        this.baseSecondMagicNumber = this.secondMagicNumber = loses[rankIndex];

        if (this.secondMagicNumber > 0) {
            this.myBaseDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        } else {
            this.myBaseDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        }
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

        this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));

        if (this.secondMagicNumber > 0) {
            this.addToBot(new ApplyPowerAction(p, p, new LoseStrengthPower(p, this.secondMagicNumber), this.secondMagicNumber));
        }
    }

    @Override
    public void performUpgradeEffect() {
        calculateStats();
        this.upgradedMagicNumber = true;
        this.upgradedSecondMagicNumber = true;
    }

    @Override
    protected void onRankLoaded() {
        calculateStats();
    }
}