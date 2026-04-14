package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.LoseStrengthPower; // 【新增导入】用于回合结束扣除力量
import com.megacrit.cardcrawl.powers.StrengthPower;

public class KuLiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("KuLiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/KuLiGu.png");

    private static final int COST = 0;
    private static final int THRESHOLD = 6;
    private static final int UPGRADE_THRESHOLD = -2;
    private static final int INITIAL_RANK = 4;

    private boolean showDynamicText = false;

    public KuLiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.LI_DAO);

        this.baseMagicNumber = this.magicNumber = THRESHOLD;
        this.baseSecondMagicNumber = this.secondMagicNumber = 0;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int amount = calculateStrengthAmount();
        if (amount > 0) {
            this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, amount), amount));
            this.addToBot(new ApplyPowerAction(p, p, new LoseStrengthPower(p, amount), amount));
        }
    }

    private int calculateStrengthAmount() {
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) return 0;
        int missingHp = AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth;
        if (missingHp < 0) missingHp = 0;
        return missingHp / this.magicNumber;
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
        int amount = calculateStrengthAmount();
        if (this.secondMagicNumber != amount) {
            this.secondMagicNumber = amount;
            this.isSecondMagicNumberModified = true;
        }

        this.showDynamicText = true;
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int amount = calculateStrengthAmount();
        if (this.secondMagicNumber != amount) {
            this.secondMagicNumber = amount;
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
            this.upgradeMagicNumber(UPGRADE_THRESHOLD);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}