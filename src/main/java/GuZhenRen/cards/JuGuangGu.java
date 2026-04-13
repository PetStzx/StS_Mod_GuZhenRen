package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.powers.ShanYaoPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JuGuangGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("JuGuangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JuGuangGu.png");

    private static final int COST = 1;
    private static final int MAGIC = 1;
    private static final int UPGRADE_PLUS_MAGIC = 1;
    private static final int INITIAL_RANK = 3;

    private boolean showDynamicText = false;

    public JuGuangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.GUANG_DAO);
        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.baseSecondMagicNumber = this.secondMagicNumber = 0;
        this.setRank(INITIAL_RANK);
    }

    private int calculateShanYaoAmount() {
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) return 0;

        int count = 0;
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.hasTag(GuZhenRenTags.GUANG_DAO)) {
                count++;
            }
        }

        if (AbstractDungeon.player.limbo.contains(this) && this.hasTag(GuZhenRenTags.GUANG_DAO)) {
            count++;
        }

        return count * this.magicNumber;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int totalShanYao = calculateShanYaoAmount();
        if (totalShanYao > 0) {
            this.addToBot(new ApplyPowerAction(p, p, new ShanYaoPower(p, totalShanYao), totalShanYao));
        }
    }

    @Override
    protected String constructRawDescription() {
        String s = super.constructRawDescription();
        if (this.showDynamicText && cardStrings.EXTENDED_DESCRIPTION != null) {
            s += cardStrings.EXTENDED_DESCRIPTION[0];
        }
        return s;
    }

    @Override
    public void applyPowers() {
        int amount = calculateShanYaoAmount();
        if (this.secondMagicNumber != amount) {
            this.secondMagicNumber = amount;
            this.isSecondMagicNumberModified = true;
        }
        this.showDynamicText = true;
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int amount = calculateShanYaoAmount();
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
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}