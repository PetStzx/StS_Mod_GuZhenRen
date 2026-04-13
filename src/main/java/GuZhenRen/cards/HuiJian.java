package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.JianFengPower;
import GuZhenRen.powers.NianPower;
import GuZhenRen.powers.QingPower;
import GuZhenRen.powers.ZhuanYiPower; // 【修复】：导入了 ZhuanYiPower
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HuiJian extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuiJian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuiJian.png");

    private static final int COST = 1;
    private static final int DAMAGE = 9;
    private static final int UPGRADE_PLUS_DAMAGE = 3; // 升级变 12 伤
    private static final int MAGIC = 5; // 5 层念
    private static final int INITIAL_RANK = 7;

    public HuiJian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.ENEMY);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);

        this.baseDamage = this.damage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));

        // 2. 获得 5 层念
        this.addToBot(new ApplyPowerAction(p, p, new NianPower(p, this.magicNumber), this.magicNumber));

        // 3. 将 1 层情转化为 1 层剑锋
        if (p.hasPower(QingPower.POWER_ID)) {
            AbstractPower qing = p.getPower(QingPower.POWER_ID);
            if (qing.amount > 0) {
                // 扣除 1 层情
                this.addToBot(new ReducePowerAction(p, p, qing, 1));
                // 给予 1 层剑锋
                this.addToBot(new ApplyPowerAction(p, p, new JianFengPower(p, 1), 1));
                // 触发底层转化引擎的联动动作
                this.addToBot(new ZhuanYiPower.TriggerAction());
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}