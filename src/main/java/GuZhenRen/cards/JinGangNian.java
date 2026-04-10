package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class JinGangNian extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("JinGangNian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JinGangNian.png");

    private static final int COST = -1;
    private static final int BASE_DMG = 6;
    private static final int MULTIPLIER = 1;
    private static final int INITIAL_RANK = 6;

    private boolean showDynamicText = false;

    public JinGangNian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.ZHI_DAO);

        this.baseDamage = BASE_DMG;
        this.baseSecondMagicNumber = this.secondMagicNumber = MULTIPLIER;
        this.baseMagicNumber = this.magicNumber = 0; // baseMagicNumber 初始为 0

        this.setRank(INITIAL_RANK);
    }

    private int calculateHits() {
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) return 0;
        int effect = EnergyPanel.totalCount;
        if (AbstractDungeon.player.hasRelic("Chemical X")) {
            effect += 2;
        }
        if (this.upgraded) {
            effect += 1;
        }
        return effect;
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
    public void update() {
        super.update();
        if (AbstractDungeon.isPlayerInDungeon() && AbstractDungeon.player != null) {
            int hits = calculateHits();
            if (this.magicNumber != hits) {
                this.magicNumber = hits;
                this.isMagicNumberModified = (this.magicNumber != this.baseMagicNumber);
            }
        }
    }

    @Override
    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += NianPower.getNianGainedThisTurn() * this.secondMagicNumber;

        int hits = calculateHits();
        if (this.magicNumber != hits) {
            this.magicNumber = hits;
            this.isMagicNumberModified = (this.magicNumber != this.baseMagicNumber);
        }

        super.applyPowers();

        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;

        if (!this.showDynamicText) {
            this.showDynamicText = true;
            this.initializeDescription();
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += NianPower.getNianGainedThisTurn() * this.secondMagicNumber;

        int hits = calculateHits();
        if (this.magicNumber != hits) {
            this.magicNumber = hits;
            this.isMagicNumberModified = (this.magicNumber != this.baseMagicNumber);
        }

        super.calculateCardDamage(mo);

        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;

        if (!this.showDynamicText) {
            this.showDynamicText = true;
            this.initializeDescription();
        }
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
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new JinGangNianAction(p, m, this.damage, this.freeToPlayOnce, this.energyOnUse, this.upgraded));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1);
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    public static class JinGangNianAction extends AbstractGameAction {
        private boolean freeToPlayOnce;
        private AbstractPlayer p;
        private AbstractMonster m;
        private int energyOnUse;
        private int damageAmount;
        private boolean isUpgraded;

        public JinGangNianAction(AbstractPlayer p, AbstractMonster m, int damageAmount, boolean freeToPlayOnce, int energyOnUse, boolean isUpgraded) {
            this.p = p;
            this.m = m;
            this.damageAmount = damageAmount;
            this.freeToPlayOnce = freeToPlayOnce;
            this.energyOnUse = energyOnUse;
            this.isUpgraded = isUpgraded;
        }

        @Override
        public void update() {
            int effect = EnergyPanel.totalCount;
            if (this.energyOnUse != -1) {
                effect = this.energyOnUse;
            }
            if (this.p.hasRelic("Chemical X")) {
                effect += 2;
                this.p.getRelic("Chemical X").flash();
            }
            if (this.isUpgraded) {
                effect += 1;
            }

            if (effect > 0) {
                for (int i = 0; i < effect; i++) {
                    this.addToTop(new DamageAction(
                            this.m,
                            new DamageInfo(this.p, this.damageAmount, DamageInfo.DamageType.NORMAL),
                            AbstractGameAction.AttackEffect.BLUNT_LIGHT
                    ));
                }
                if (!this.freeToPlayOnce) {
                    this.p.energy.use(EnergyPanel.totalCount);
                }
            }
            this.isDone = true;
        }
    }
}