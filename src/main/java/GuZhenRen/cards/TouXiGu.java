package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class TouXiGu extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("TouXiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/TouXiGu.png");

    private static final int COST = 0;
    private static final int DAMAGE = 3; // 基础伤害 3
    private static final int MAGIC = 1;

    private static final int INITIAL_RANK = 2;

    public TouXiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ENEMY);

        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;

        this.setDao(Dao.TOU_DAO);
        this.setRank(INITIAL_RANK);

        // 使用 StSLib 的伤害修改器实现“无视格挡”攻击
        DamageModifierManager.addModifier(this, new IgnoreBlockModifier());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 升级后打 2 次
        int times = this.upgraded ? 2 : 1;

        for (int i = 0; i < times; i++) {
            this.addToBot(new DamageAction(m,
                    new DamageInfo(p, this.damage, this.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        }

        // 给予易伤
        this.addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, this.magicNumber, false), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1); // 2转 -> 3转
            this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    // ==========================================================
    // 内部类：StSLib 无视格挡伤害修改器
    // ==========================================================
    public static class IgnoreBlockModifier extends AbstractDamageModifier {
        @Override
        public boolean ignoresBlock(AbstractCreature target) {
            return true; // 无视格挡
        }

        @Override
        public AbstractDamageModifier makeCopy() {
            return new IgnoreBlockModifier();
        }
    }
}