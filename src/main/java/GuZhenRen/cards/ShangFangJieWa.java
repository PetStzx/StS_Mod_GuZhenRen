package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class ShangFangJieWa extends AbstractShaZhaoCard {

    public static final String ID = GuZhenRen.makeID("ShangFangJieWa");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ShangFangJieWa.png");

    private static final int COST = 1;
    private static final int BASE_DAMAGE = 10;
    private static final int MULTIPLIER = 3; // 3倍力量加成

    // 原版游戏中需要被驱散的防御类状态 ID 列表
    private static final String[] DEFENSIVE_POWERS = {
            "Intangible", // 无实体
            "IntangiblePlayer", // 无实体
            "Invincible", // 坚不可摧
            "Malleable", // 多重护甲
            "Metallicize", // 金属化
            "Barricade", // 壁垒
            "Curl Up", // 蜷身
            "Plated Armor" // 柔韧
    };

    public ShangFangJieWa() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ENEMY);

        this.baseDamage = BASE_DAMAGE;
        this.baseMagicNumber = this.magicNumber = MULTIPLIER;

        // 杀招流派设定
        this.setDao(Dao.LI_DAO);
    }

    // =========================================================================
    // 单体 3 倍力量加成机制
    // =========================================================================
    @Override
    public void applyPowers() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            int extraStr = 0;
            if (p.hasPower(StrengthPower.POWER_ID)) {
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

    // =========================================================================
    // 效果执行逻辑
    // =========================================================================
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            // 1. 去除所有格挡
            this.addToBot(new RemoveAllBlockAction(m, p));

            // 2. 遍历并驱散目标身上的特定防御类状态
            for (String powerID : DEFENSIVE_POWERS) {
                if (m.hasPower(powerID)) {
                    this.addToBot(new RemoveSpecificPowerAction(m, p, powerID));
                }
            }

            // 用延迟动作重新计算伤害
            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    calculateCardDamage(m);

                    this.addToTop(new DamageAction(
                            m,
                            new DamageInfo(p, damage, damageTypeForTurn),
                            AbstractGameAction.AttackEffect.BLUNT_HEAVY
                    ));

                    this.isDone = true;
                }
            });
        }
    }
}