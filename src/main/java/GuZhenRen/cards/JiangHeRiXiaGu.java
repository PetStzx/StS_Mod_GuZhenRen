package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JiangHeRiXiaGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("JiangHeRiXiaGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JiangHeRiXiaGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 4;
    private static final int UPGRADE_PLUS_DMG = 2;
    private static final int INITIAL_RANK = 4;

    private boolean showDynamicText = false;

    public JiangHeRiXiaGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.GUANG_DAO);
        this.baseDamage = DAMAGE;
        this.baseSecondMagicNumber = this.secondMagicNumber = 0;
        this.isMultiDamage = true;
        this.setRank(INITIAL_RANK);
    }

    private int calculateHits() {
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

        return count;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int hits = calculateHits();
        if (hits > 0) {
            for (int i = 0; i < hits; i++) {
                this.addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
            }
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
        int amount = calculateHits();
        if (this.secondMagicNumber != amount) {
            this.secondMagicNumber = amount;
            this.isSecondMagicNumberModified = true;
        }
        this.showDynamicText = true;
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int amount = calculateHits();
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
            this.upgradeDamage(UPGRADE_PLUS_DMG);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}