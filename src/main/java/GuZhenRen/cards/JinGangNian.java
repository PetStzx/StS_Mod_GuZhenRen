package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
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

        // 【核心修改】抛弃 magicNumber，启用专属念变量
        this.baseNian = this.nian = NIAN_AMT;

        this.setRank(INITIAL_RANK);
    }

    // 【删除】丑陋的旧版 applyPowers 已被全部抹除

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 【极简】传入算好加成的 this.nian
        this.addToBot(new JinGangNianAction(p, this.nian, this.damage, this.freeToPlayOnce, this.energyOnUse));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeNian(UPGRADE_NIAN_AMT); // 使用专属升级方法
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    // =========================================================================
    // 统筹结算的 X 费 Action
    // =========================================================================
    public static class JinGangNianAction extends AbstractGameAction {
        private boolean freeToPlayOnce;
        private AbstractPlayer p;
        private int energyOnUse;
        private int nianAmount; // 这是加成后的实际层数
        private int damageAmount;

        public JinGangNianAction(AbstractPlayer p, int nianAmount, int damageAmount, boolean freeToPlayOnce, int energyOnUse) {
            this.p = p;
            this.nianAmount = nianAmount;
            this.damageAmount = damageAmount;
            this.freeToPlayOnce = freeToPlayOnce;
            this.energyOnUse = energyOnUse;
        }

        @Override
        public void update() {
            int effect = EnergyPanel.totalCount;
            if (this.energyOnUse != -1) {
                effect = this.energyOnUse;
            }
            if (this.p.hasRelic("Chemical X")) {
                effect += 2;
                this.p.getRelic("Chemical X").flash();
            }

            if (effect > 0) {
                for (int i = 0; i < effect; i++) {
                    // 把加成后的 nianAmount 传给子动作
                    AbstractDungeon.actionManager.addToBottom(new JinGangNianSingleAction(p, nianAmount, damageAmount));
                }
                if (!this.freeToPlayOnce) {
                    this.p.energy.use(EnergyPanel.totalCount);
                }
            }
            this.isDone = true;
        }
    }

    public static class JinGangNianSingleAction extends AbstractGameAction {
        private final AbstractPlayer p;
        private final int nianAmount;
        private final int damageAmount;

        public JinGangNianSingleAction(AbstractPlayer p, int nianAmount, int damageAmount) {
            this.p = p;
            this.nianAmount = nianAmount;
            this.damageAmount = damageAmount;
            this.actionType = ActionType.SPECIAL;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            int actualGain = this.nianAmount;

            // 处理转换限制（例如被智障能力拦截）
            if (NianPower.isConverted(p)) {
                actualGain = 0;
            }

            if (actualGain > 0) {
                // 根据实际获得的层数，进行多次飞弹伤害
                for (int i = 0; i < actualGain; i++) {
                    this.addToTop(new DamageRandomEnemyAction(
                            new DamageInfo(p, damageAmount, DamageInfo.DamageType.NORMAL),
                            AbstractGameAction.AttackEffect.BLUNT_LIGHT
                    ));
                }
                // 给予纯净的 NianPower
                this.addToTop(new ApplyPowerAction(p, p, new NianPower(p, actualGain), actualGain));
            }

            this.isDone = true;
        }
    }
}