package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import GuZhenRen.powers.QingPower; // 导入情
import GuZhenRen.powers.ZhiDaoDaoHenPower; // 导入智道道痕
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class JinGangNian extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("JinGangNian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JinGangNian.png");

    private static final int COST = -1; // X费
    private static final int DAMAGE = 2;
    private static final int NIAN_AMT = 2;
    private static final int UPGRADE_NIAN_AMT = 1;
    private static final int INITIAL_RANK = 6;

    public JinGangNian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.ZHI_DAO);

        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = NIAN_AMT;

        this.setRank(INITIAL_RANK);
    }

    // =========================================================================
    //  【新增】 动态修改卡面显示的数值 (为了让玩家看到 !M! 变绿并显示实际值)
    // =========================================================================
    @Override
    public void applyPowers() {
        // 1. 先重置为基础值
        this.magicNumber = this.baseMagicNumber;

        // 2. 调用父类 (处理 rank lock 等)
        super.applyPowers();

        // 3. 计算加成 (逻辑与 ZhanNianGu 一致)
        int bonus = 0;

        // 计算【情】的加成
        if (AbstractDungeon.player.hasPower(QingPower.POWER_ID)) {
            // 【修正】 改为 / 3
            bonus += AbstractDungeon.player.getPower(QingPower.POWER_ID).amount / 3;
        }

        // 计算【智道道痕】的加成
        if (AbstractDungeon.player.hasPower(ZhiDaoDaoHenPower.POWER_ID)) {
            // 【修正】 改为 / 3
            bonus += AbstractDungeon.player.getPower(ZhiDaoDaoHenPower.POWER_ID).amount / 3;
        }

        // 4. 应用加成到显示数值
        if (bonus > 0) {
            this.magicNumber += bonus;
            this.isMagicNumberModified = true;
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.energyOnUse < -1) {
            this.energyOnUse = -1;
        }

        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }

        if (p.hasRelic(ChemicalX.ID)) {
            effect += 2;
            p.getRelic(ChemicalX.ID).flash();
        }

        if (effect > 0) {
            for (int i = 0; i < effect; i++) {
                // 【核心修改】 这里必须传 this.baseMagicNumber (基础值 2)
                this.addToBot(new JinGangNianSingleAction(p, this.baseMagicNumber, this.damage));
            }
        }

        if (!this.freeToPlayOnce) {
            p.energy.use(EnergyPanel.totalCount);
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_NIAN_AMT);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    public static class JinGangNianSingleAction extends AbstractGameAction {
        private final AbstractPlayer p;
        private final int baseNianAmount;
        private final int damageAmount;

        public JinGangNianSingleAction(AbstractPlayer p, int baseNianAmount, int damageAmount) {
            this.p = p;
            this.baseNianAmount = baseNianAmount;
            this.damageAmount = damageAmount;
            this.actionType = ActionType.SPECIAL;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            // NianPower 构造函数负责计算加成后的最终数值
            NianPower powerToApply = new NianPower(p, baseNianAmount);
            int actualGain = powerToApply.amount;

            // 【核心修复】 使用 NianPower 提供的静态方法检查是否被转化
            if (NianPower.isConverted(p)) {
                actualGain = 0;
            }

            // 造成伤害 (actualGain 为 0 时不造成伤害)
            if (actualGain > 0) {
                for (int i = 0; i < actualGain; i++) {
                    this.addToTop(new DamageRandomEnemyAction(
                            new DamageInfo(p, damageAmount, DamageInfo.DamageType.NORMAL),
                            AbstractGameAction.AttackEffect.BLUNT_LIGHT
                    ));
                }
            }

            // 施加状态 (依然需要执行，以便 NianPower 内部触发 ZhiZhang 的临时生命转化)
            this.addToTop(new ApplyPowerAction(p, p, powerToApply, powerToApply.amount));
            this.isDone = true;
        }
    }
}