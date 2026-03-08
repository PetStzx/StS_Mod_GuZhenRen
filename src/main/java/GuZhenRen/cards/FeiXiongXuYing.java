package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
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


    // 重刃机制
    @Override
    public void applyPowers() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            int extraStr = 0;
            if (p.hasPower(StrengthPower.POWER_ID)) {
                // 计算额外的倍数收益
                extraStr = p.getPower(StrengthPower.POWER_ID).amount * (this.magicNumber - 1);
            }

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
            int extraStr = 0;
            if (p.hasPower(StrengthPower.POWER_ID)) {
                extraStr = p.getPower(StrengthPower.POWER_ID).amount * (this.magicNumber - 1);
            }

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