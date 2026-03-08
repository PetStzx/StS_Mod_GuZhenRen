package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BaiXiangXuYing extends AbstractXuYingCard {
    public static final String ID = GuZhenRen.makeID("BaiXiangXuYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/BaiXiangXuYing.png");

    private static final int DAMAGE = 4;
    private static final int UPGRADE_PLUS_DAMAGE = 2; // 升级虚影伤害 4 -> 6

    public BaiXiangXuYing() {
        super(ID, NAME, IMG_PATH, -2, DESCRIPTION,
                CardType.ATTACK, // 改为攻击牌
                CardColorEnum.GUZHENREN_GREY,
                CardTarget.ENEMY);

        // 基础概率调整为 20%
        this.baseChanceFloat = 0.20f;
        this.baseDamage = this.damage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = 1; // 获得1点力量

        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            // 升级概率调整为 30%
            this.baseChanceFloat = 0.30f;
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.initializeDescription();
        }
    }

    @Override
    public void triggerPhantomEffect(AbstractMonster m) {
        if (m != null && !m.isDeadOrEscaped()) {
            // 实时计算伤害
            this.calculateCardDamage(m);

            this.addToTop(new ApplyPowerAction(
                    AbstractDungeon.player,
                    AbstractDungeon.player,
                    new StrengthPower(AbstractDungeon.player, this.magicNumber),
                    this.magicNumber
            ));

            this.addToTop(new DamageAction(
                    m,
                    new DamageInfo(AbstractDungeon.player, this.damage, DamageInfo.DamageType.NORMAL),
                    AbstractGameAction.AttackEffect.BLUNT_HEAVY
            ));
        }
    }
}