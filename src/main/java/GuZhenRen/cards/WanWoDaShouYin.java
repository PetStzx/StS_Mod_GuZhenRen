package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WanWoDaShouYin extends AbstractShaZhaoCard {

    public static final String ID = GuZhenRen.makeID("WanWoDaShouYin");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WanWoDaShouYin.png");

    private static final int COST = 2;
    private static final int BASE_DAMAGE = 24;

    public WanWoDaShouYin() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ALL_ENEMY);

        this.baseDamage = BASE_DAMAGE;
        this.isMultiDamage = true; // 标记为群体伤害

        // 杀招流派设定
        this.setDao(Dao.LI_DAO);
    }

    // =========================================================================
    // 满血三倍伤害
    // =========================================================================
    @Override
    public void applyPowers() {
        super.applyPowers(); // 先让引擎计算所有的力量、虚弱等常规加成

        if (this.multiDamage != null) {
            // 遍历房间里的所有怪物，单独修改伤害数组
            for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);
                if (m != null && !m.isDeadOrEscaped() && m.currentHealth == m.maxHealth) {
                    this.multiDamage[i] *= 3; // 满血怪受到 3 倍伤害
                }
            }
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);

        if (this.multiDamage != null) {
            for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);
                if (m != null && !m.isDeadOrEscaped() && m.currentHealth == m.maxHealth) {
                    this.multiDamage[i] *= 3;
                }
            }
        }

        // 当玩家鼠标悬停在某一只特定的怪物上时，修改牌面显示的绿色大数字
        if (mo != null && mo.currentHealth == mo.maxHealth) {
            this.damage *= 3;
        }

        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAllEnemiesAction(
                p,
                this.multiDamage,
                this.damageTypeForTurn,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        ));
    }
}