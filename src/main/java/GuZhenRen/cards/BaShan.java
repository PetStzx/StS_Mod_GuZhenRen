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

    private static final int MAGIC = 3; // 力量发挥的倍数
    private static final int UPGRADE_PLUS_MAGIC = 2; // 升级加 2，变成 5 倍

    private static final int INITIAL_RANK = 6; // 6转仙蛊

    public BaShan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ALL_ENEMY);

        this.baseDamage = BASE_DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.isMultiDamage = true; // 开启群体伤害判定

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
    }

    //力量倍数加成
    @Override
    public void applyPowers() {
        AbstractPlayer p = AbstractDungeon.player;
        int realBaseDamage = this.baseDamage;

        if (p != null && p.hasPower(StrengthPower.POWER_ID)) {
            int strAmt = p.getPower(StrengthPower.POWER_ID).amount;
            this.baseDamage += strAmt * (this.magicNumber - 1);
        }

        super.applyPowers();

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
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}