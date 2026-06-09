package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.AbstractDaoHenPower;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class LongLinGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LongLinGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LongLinGu.png");

    private static final String JIAN_FENG_ID = GuZhenRen.makeID("JianFengPower");

    private static final int COST = 1;
    private static final int BASE_BLOCK = 2;
    private static final int BASE_TIMES = 3;
    private static final int UPGRADE_PLUS_TIMES = 1;
    private static final int INITIAL_RANK = 7;

    private boolean showDynamicText = false;

    public LongLinGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.BIAN_HUA_DAO);
        this.setRank(INITIAL_RANK);

        this.baseBlock = this.block = BASE_BLOCK;
        this.baseMagicNumber = this.magicNumber = BASE_TIMES;
        this.baseSecondMagicNumber = this.secondMagicNumber = 0;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int totalTimes = calculateTotalTimes();

        totalTimes = Math.max(0, totalTimes);

        for (int i = 0; i < totalTimes; i++) {
            this.addToBot(new GainBlockAction(p, p, this.block));
        }
    }

    private int calculateTotalTimes() {
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) {
            return this.magicNumber;
        }

        int extraCount = 0;

        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof AbstractDaoHenPower || p.ID.equals(JIAN_FENG_ID)) {
                extraCount += p.amount;
            }
        }

        return this.magicNumber + extraCount;
    }

    @Override
    protected String constructRawDescription() {
        String s = super.constructRawDescription();
        if (this.showDynamicText) {
            s += cardStrings.EXTENDED_DESCRIPTION[0];
        }
        return s;
    }

    @Override
    public void applyPowers() {
        int totalTimes = calculateTotalTimes();
        if (this.secondMagicNumber != totalTimes) {
            this.secondMagicNumber = totalTimes;
            this.isSecondMagicNumberModified = true;
        }
        this.showDynamicText = true;
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int totalTimes = calculateTotalTimes();
        if (this.secondMagicNumber != totalTimes) {
            this.secondMagicNumber = totalTimes;
            this.isSecondMagicNumberModified = true;
        }
        this.showDynamicText = true;
        super.calculateCardDamage(mo);
    }

    @Override
    public void onMoveToDiscard() {
        this.showDynamicText = false;
        this.initializeDescription();
    }

    @Override
    public void triggerOnExhaust() {
        this.showDynamicText = false;
        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_TIMES);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}