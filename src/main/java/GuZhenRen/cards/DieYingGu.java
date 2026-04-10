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
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/DieYingGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 8;
    private static final int JIAN_HEN = 1; // 基础剑痕层数
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

        this.baseMagicNumber = this.magicNumber = JIAN_HEN;

        this.baseSecondMagicNumber = this.secondMagicNumber = 2;

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
    // 动态更新逻辑：修改伤害和剑痕层数
    // ==========================================================
    @Override
    public void applyPowers() {
        int count = countJianYingInExhaust();

        // 1. 动态更新剑痕层数
        this.magicNumber = this.baseMagicNumber + (count * this.secondMagicNumber);
        this.isMagicNumberModified = (this.magicNumber != this.baseMagicNumber);

        // 2. 动态更新伤害
        int realBaseDamage = this.baseDamage;
        this.baseDamage += count * this.secondMagicNumber;
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int count = countJianYingInExhaust();

        // 1. 动态更新剑痕层数
        this.magicNumber = this.baseMagicNumber + (count * this.secondMagicNumber);
        this.isMagicNumberModified = (this.magicNumber != this.baseMagicNumber);

        // 2. 动态更新伤害
        int realBaseDamage = this.baseDamage;
        this.baseDamage += count * this.secondMagicNumber;
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void resetAttributes() {
        super.resetAttributes();
        this.magicNumber = this.baseMagicNumber;
        this.isMagicNumberModified = false;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 打出总伤害
        this.addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HEAVY));

        // 赋予总剑痕
        if (this.magicNumber > 0) {
            this.addToBot(new ApplyPowerAction(m, p, new JianHenPower(m, this.magicNumber), this.magicNumber));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1);

            this.upgradeSecondMagicNumber(1);

            AbstractCard upgradedPreview = new JianYing();
            upgradedPreview.upgrade();
            this.cardsToPreview = upgradedPreview;

            this.initializeDescription();
        }
    }
}