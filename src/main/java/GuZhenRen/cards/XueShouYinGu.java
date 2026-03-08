package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XueShouYinGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("XueShouYinGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueShouYinGu.png");

    private static final int COST = 2;
    private static final int DAMAGE = 14;
    private static final int UPGRADE_PLUS_DAMAGE = 4; // 升级加 4 伤 (14 -> 18)

    private static final int MAGIC = 10; // 目标每有 10 点生命
    private static final int UPGRADE_NEW_MAGIC = 8; // 升级后变 8 点生命

    private static final int INITIAL_RANK = 4;

    public XueShouYinGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.ENEMY);

        this.setDao(Dao.XUE_DAO);

        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    // =========================================================================
    // 鼠标悬停在特定怪物上时，动态将额外伤害加入总伤害中
    // =========================================================================
    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        if (mo != null) {
            // 根据怪物的当前生命值增加基础伤害
            this.baseDamage += mo.currentHealth / this.magicNumber;
        }

        super.calculateCardDamage(mo);

        // 还原基础伤害，确保逻辑闭环
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.baseMagicNumber = UPGRADE_NEW_MAGIC;
            this.magicNumber = this.baseMagicNumber;
            this.upgradedMagicNumber = true;
            this.upgradeRank(1); // 4转 -> 5转
            this.initializeDescription();
        }
    }
}