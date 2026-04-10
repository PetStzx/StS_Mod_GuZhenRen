package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class QunLiGu extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("QunLiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/QunLiGu.png");

    private static final int COST = 1;
    private static final int BASE_DAMAGE = 8;
    private static final int UPGRADE_PLUS_DMG = 2;  // 升级后伤害 8 -> 10

    // 魔法值代表：每张虚影牌提供的额外伤害
    private static final int BASE_MAGIC = 8;
    private static final int UPGRADE_PLUS_MAGIC = 2; // 升级后额外伤害 8 -> 10

    private static final int INITIAL_RANK = 5; // 5转

    public QunLiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.baseDamage = BASE_DAMAGE;
        this.baseMagicNumber = this.magicNumber = BASE_MAGIC;

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
    }

    // =========================================================================
    // 辅助方法：统计当前手牌中有几张虚影牌
    // =========================================================================
    private int countPhantomsInHand() {
        int count = 0;
        if (AbstractDungeon.player != null) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                // 只要是继承了虚影父类的牌，就算数
                if (c instanceof AbstractXuYingCard) {
                    count++;
                }
            }
        }
        return count;
    }

    // =========================================================================
    // 动态伤害计算逻辑
    // =========================================================================
    @Override
    public void applyPowers() {
        int realBaseDamage = this.baseDamage;

        // 算出额外加成，临时垫入基础伤害中
        int extraDamage = countPhantomsInHand() * this.magicNumber;
        this.baseDamage += extraDamage;

        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;

        int extraDamage = countPhantomsInHand() * this.magicNumber;
        this.baseDamage += extraDamage;

        super.calculateCardDamage(mo);

        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    // =========================================================================
    // 打出结算
    // =========================================================================
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(
                m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        ));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DMG);
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1); // 5转 -> 6转
            this.initializeDescription();
        }
    }
}