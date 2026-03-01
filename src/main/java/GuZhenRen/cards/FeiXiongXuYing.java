package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.LiDaoDaoHenPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class FeiXiongXuYing extends AbstractXuYingCard {
    public static final String ID = GuZhenRen.makeID("FeiXiongXuYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/FeiXiongXuYing.png");

    private static final int BASE_DAMAGE = 5;

    public FeiXiongXuYing() {
        super(ID, NAME, IMG_PATH, -2, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardTarget.ALL_ENEMY);

        this.baseChanceFloat = 0.25f; // 固定25%触发率

        this.baseDamage = this.damage = BASE_DAMAGE;
        this.isMultiDamage = true; // 开启群体伤害

        // 魔法值用来代表“力量倍数”，基础2倍
        this.baseMagicNumber = this.magicNumber = 2;

        this.initializeDescription();
    }

    /**
     * 获取力量与力道道痕的总和
     */
    private int getStrengthBonus(AbstractPlayer p) {
        int bonus = 0;
        if (p.hasPower(StrengthPower.POWER_ID)) {
            bonus += p.getPower(StrengthPower.POWER_ID).amount;
        }
        if (p.hasPower(LiDaoDaoHenPower.POWER_ID)) {
            bonus += p.getPower(LiDaoDaoHenPower.POWER_ID).amount;
        }
        return bonus;
    }

    // =========================================================================
    // 【核心逻辑】重刃机制
    // 我们加上了 (力量 * (倍数-1))，然后 super.applyPowers() 会再自动算上正常的 1 倍。
    // 最终正好等于 (力量 * 倍数)！这也完美兼容了虚弱等百分比Debuff。
    // =========================================================================
    @Override
    public void applyPowers() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            int extraStr = getStrengthBonus(p) * (this.magicNumber - 1);
            int realBaseDamage = this.baseDamage;

            this.baseDamage += extraStr;
            super.applyPowers();

            this.baseDamage = realBaseDamage;
            this.isDamageModified = (this.damage != this.baseDamage);
        } else {
            super.applyPowers();
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            int extraStr = getStrengthBonus(p) * (this.magicNumber - 1);
            int realBaseDamage = this.baseDamage;

            this.baseDamage += extraStr;
            super.calculateCardDamage(mo);

            this.baseDamage = realBaseDamage;
            this.isDamageModified = (this.damage != this.baseDamage);
        } else {
            super.calculateCardDamage(mo);
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1); // 力量倍数 2 -> 3
            this.initializeDescription();
        }
    }

    @Override
    public void triggerPhantomEffect(AbstractMonster m) {
        if (AbstractDungeon.player != null) {
            // 实时重算一次群体伤害数组
            this.applyPowers();

            this.addToTop(new DamageAllEnemiesAction(
                    AbstractDungeon.player,
                    this.multiDamage,
                    this.damageTypeForTurn,
                    AbstractGameAction.AttackEffect.BLUNT_HEAVY
            ));
        }
    }
}