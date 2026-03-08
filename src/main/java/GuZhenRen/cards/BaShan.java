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

public class BaShan extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("BaShan");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/BaShan.png");

    private static final int COST = 2;
    private static final int BASE_DAMAGE = 15;
    private static final int UPGRADE_PLUS_DAMAGE = 5; // 升级加5点，变成20
    private static final int INITIAL_RANK = 6; // 6转仙蛊

    public BaShan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.ALL_ENEMY);

        this.baseDamage = BASE_DAMAGE;
        this.isMultiDamage = true; // 群体伤害

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
    }

    // =========================================================================
    // 动态计算群体伤害数组，满血翻倍
    // =========================================================================
    @Override
    public void applyPowers() {
        super.applyPowers(); // 先让引擎计算所有的力量、虚弱加成

        if (this.multiDamage != null) {
            // 遍历房间里的所有怪物，单独修改伤害数组
            for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
                AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);
                if (m != null && !m.isDeadOrEscaped() && m.currentHealth == m.maxHealth) {
                    this.multiDamage[i] *= 2; // 如果满血，这只怪的受到的伤害翻倍
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
                    this.multiDamage[i] *= 2;
                }
            }
        }

        if (mo != null && mo.currentHealth == mo.maxHealth) {
            this.damage *= 2;
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

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE); // 15 -> 20
            this.upgradeRank(1); // 6转 -> 7转
            this.initializeDescription();
        }
    }
}