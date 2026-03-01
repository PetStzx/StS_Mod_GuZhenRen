package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.JianHenPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DieYingGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("DieYingGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/DieYingGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 2; // 升级后基础变 8

    // 移除 DMG_MULTIPLIER，改用 SecondMagicNumber

    private static final int INITIAL_RANK = 4;

    public DieYingGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);
        this.baseDamage = DAMAGE;

        // baseMagicNumber 专门用来代表“动态剑痕层数”，初始是 0
        this.baseMagicNumber = 0;
        this.magicNumber = 0;

        // baseSecondMagicNumber 用来代表每张剑影提供的额外伤害
        this.baseSecondMagicNumber = 2;
        this.secondMagicNumber = 2;

        this.cardsToPreview = new JianYing();
    }

    private int countJianYingInExhaust() {
        int count = 0;
        if (AbstractDungeon.player != null && AbstractDungeon.player.exhaustPile != null) {
            for (AbstractCard c : AbstractDungeon.player.exhaustPile.group) {
                if (c.cardID.equals(JianYing.ID)) {
                    count++;
                }
            }
        }
        return count;
    }

    // ==========================================================
    // 动态更新逻辑
    // ==========================================================
    @Override
    public void applyPowers() {
        int count = countJianYingInExhaust();

        // 动态更新代表剑痕层数的 magicNumber
        this.baseMagicNumber = count;
        this.magicNumber = count;
        this.isMagicNumberModified = (count > 0);

        // 动态更新伤害，直接乘以 secondMagicNumber
        int realBaseDamage = this.baseDamage;
        this.baseDamage += count * this.secondMagicNumber;
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int count = countJianYingInExhaust();

        this.baseMagicNumber = count;
        this.magicNumber = count;
        this.isMagicNumberModified = (count > 0);

        // 动态更新伤害，直接乘以 secondMagicNumber
        int realBaseDamage = this.baseDamage;
        this.baseDamage += count * this.secondMagicNumber;
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    // 当离开手牌时，将 magicNumber 规矩地重置为 0
    @Override
    public void resetAttributes() {
        super.resetAttributes();
        this.baseMagicNumber = 0;
        this.magicNumber = 0;
        this.isMagicNumberModified = false;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 打出总伤害
        this.addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HEAVY));

        if (this.magicNumber > 0) {
            this.addToBot(new ApplyPowerAction(m, p, new JianHenPower(m, this.magicNumber), this.magicNumber));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DMG);
            this.upgradeRank(1);

            // 升级第二魔法值：伤害倍率 2 -> 3
            this.upgradeSecondMagicNumber(1);

            AbstractCard upgradedPreview = new JianYing();
            upgradedPreview.upgrade();
            this.cardsToPreview = upgradedPreview;

            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}