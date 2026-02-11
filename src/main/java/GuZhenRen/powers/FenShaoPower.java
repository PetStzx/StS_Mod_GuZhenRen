package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class FenShaoPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("FenShaoPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 保留原构造函数，默认允许加成 (为了兼容其他卡牌)
    public FenShaoPower(AbstractCreature owner, int amount) {
        this(owner, amount, false); // 默认 isPassive = false
    }

    // 【新增】重载构造函数，增加 isPassive 参数
    // isPassive = true 表示这是被动/特效触发，不吃道痕加成
    public FenShaoPower(AbstractCreature owner, int amount, boolean isPassive) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;

        this.amount = amount;

        // 【核心修改】只有在非被动触发时，才计算构造时的加成
        if (!isPassive) {
            int bonus = getYanDaoBonus();
            if (bonus > 0) {
                this.amount += bonus;
            }
        }

        this.type = PowerType.DEBUFF;
        this.isTurnBased = false;
        this.loadRegion("flameBarrier");

        this.updateDescription();
    }

    // 获取加成数值
    private int getYanDaoBonus() {
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(YanDaoDaoHenPower.POWER_ID)) {
            int daoHenAmt = AbstractDungeon.player.getPower(YanDaoDaoHenPower.POWER_ID).amount;
            return daoHenAmt / 2;
        }
        return 0;
    }

    @Override
    public void onInitialApplication() {
        triggerBurningDamage();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;

        // 【核心修改】检测当前动作的来源
        // 如果触发叠加的动作来源不是玩家 (比如是敌人自己触发了追命火)，则不享受加成
        boolean applyBonus = true;
        AbstractGameAction action = AbstractDungeon.actionManager.currentAction;
        if (action != null && action.source != null && action.source != AbstractDungeon.player) {
            applyBonus = false;
        }

        int bonus = 0;
        if (applyBonus) {
            bonus = getYanDaoBonus();
        }

        // 计算实际叠加量
        int totalStack = stackAmount + bonus;

        this.amount += totalStack;

        triggerBurningDamage();
        this.updateDescription();
    }

    private void triggerBurningDamage() {
        if (this.owner != null && !this.owner.isDeadOrEscaped()) {
            this.flash();
            this.addToBot(new LoseHPAction(this.owner, this.owner, this.amount, AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}