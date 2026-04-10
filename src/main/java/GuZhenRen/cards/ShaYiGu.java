package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.YiPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

public class ShaYiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ShaYiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ShaYiGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 7;
    private static final int UPGRADE_DAMAGE_AMT = 3; // 7 -> 10
    private static final int YI_AMT = 1;
    private static final int INITIAL_RANK = 3;

    public ShaYiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON, // 白卡
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.ZHI_DAO);

        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = YI_AMT;
        this.isMultiDamage = true;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 统计当前活着的敌人数量
        int enemyCount = 0;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                enemyCount++;
            }
        }

        // 2. 造成AOE伤害
        this.addToBot(new SFXAction("ATTACK_HEAVY"));
        this.addToBot(new VFXAction(p, new CleaveEffect(), 0.1F));
        this.addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));

        // 3. 给予意
        if (enemyCount > 0) {
            int totalYi = enemyCount * this.magicNumber;
            this.addToBot(new ApplyPowerAction(p, p, new YiPower(p, totalYi), totalYi));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_DAMAGE_AMT);
            this.upgradeRank(1); // 3 -> 4
            this.initializeDescription();
        }
    }
}