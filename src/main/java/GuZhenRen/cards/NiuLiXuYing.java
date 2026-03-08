package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class NiuLiXuYing extends AbstractXuYingCard {
    public static final String ID = GuZhenRen.makeID("NiuLiXuYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/NiuLiXuYing.png");

    private static final int DAMAGE = 4;

    public NiuLiXuYing() {
        super(ID, NAME, IMG_PATH, -2, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardTarget.ENEMY);

        this.baseDamage = this.damage = DAMAGE;
        this.baseChanceFloat = 0.25f;
        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.baseChanceFloat = 0.40f;
            this.initializeDescription();
        }
    }

    @Override
    public void triggerPhantomEffect(AbstractMonster m) {
        if (m != null && !m.isDeadOrEscaped()) {

            // 这行代码是实时计算的，如果你手牌左边的虚影刚刚给怪物上了易伤，这里就能吃到加成
            this.calculateCardDamage(m);

            // 确保在父类的“移除卡牌”动作之前立刻打出伤害
            AbstractDungeon.actionManager.addToTop(new DamageAction(
                    m,
                    new DamageInfo(AbstractDungeon.player, this.damage, DamageInfo.DamageType.NORMAL),
                    AbstractGameAction.AttackEffect.BLUNT_HEAVY
            ));
        }
    }
}