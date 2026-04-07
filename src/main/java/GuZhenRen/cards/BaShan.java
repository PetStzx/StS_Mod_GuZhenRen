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

public class BaShan extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("BaShan");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/BaShan.png");

    private static final int COST = 2;
    private static final int BASE_DAMAGE = 15;
    private static final int UPGRADE_PLUS_DAMAGE = 3; // 升级加 3，变成 18

    // 力量发挥的倍数
    private static final int MAGIC = 3;
    private static final int UPGRADE_PLUS_MAGIC = 2; // 升级加 2，变成 5 倍

    private static final int INITIAL_RANK = 6; // 6转仙蛊

    public BaShan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.ALL_ENEMY);

        this.baseDamage = BASE_DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.isMultiDamage = true; // 开启群体伤害判定

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
    }

    // =========================================================================
    // 动态力量倍数加成处理 (AOE逻辑)
    // =========================================================================
    @Override
    public void applyPowers() {
        AbstractPlayer p = AbstractDungeon.player;
        int realBaseDamage = this.baseDamage;

        if (p != null && p.hasPower(StrengthPower.POWER_ID)) {
            // 底层本身会加 1 倍力量，所以我们额外补充 (magicNumber - 1) 倍
            int strAmt = p.getPower(StrengthPower.POWER_ID).amount;
            this.baseDamage += strAmt * (this.magicNumber - 1);
        }

        super.applyPowers();

        // 恢复真实的面板数值，并告诉引擎数值已被修改（变色）
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        AbstractPlayer p = AbstractDungeon.player;
        int realBaseDamage = this.baseDamage;

        if (p != null && p.hasPower(StrengthPower.POWER_ID)) {
            int strAmt = p.getPower(StrengthPower.POWER_ID).amount;
            this.baseDamage += strAmt * (this.magicNumber - 1);
        }

        super.calculateCardDamage(mo);

        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    // =========================================================================
    // 打出结算
    // =========================================================================
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAllEnemiesAction(
                p,
                this.multiDamage, // 使用父类生成的 AOE 伤害数组
                this.damageTypeForTurn,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        ));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE); // 16 -> 20
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC); // 3倍 -> 5倍
            this.upgradeRank(1); // 6转 -> 7转
            this.initializeDescription();
        }
    }
}