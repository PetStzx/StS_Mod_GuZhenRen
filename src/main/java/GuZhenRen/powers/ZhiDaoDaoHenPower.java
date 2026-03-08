package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.unique.RetainCardsAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class ZhiDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("ZhiDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    // 用于跨回合记录存下的能量
    private int energyRetained = 0;

    public ZhiDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        int energyBonus = this.amount / 3;
        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1] + energyBonus + powerStrings.DESCRIPTIONS[2];
    }

    // =========================================================================
    // 核心逻辑：在回合结束（丢牌和能量清空前）触发
    // =========================================================================
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {

            // 1. 保留能量逻辑
            if (!AbstractDungeon.player.hasRelic("Ice Cream")) {
                int maxEnergyRetain = this.amount / 3; // 计算理论最大可保留值
                if (maxEnergyRetain > 0) {
                    // 实际保留的能量 = Min(当前剩余的真实能量, 理论最大可保留能量)
                    this.energyRetained = Math.min(EnergyPanel.totalCount, maxEnergyRetain);
                } else {
                    this.energyRetained = 0;
                }
            }

            // 2. 保留手牌逻辑
            // 如果手牌不为空，并且玩家没有“符文金字塔”或“均衡”能力（这些已经提供了全保留）
            if (!AbstractDungeon.player.hand.isEmpty() && !AbstractDungeon.player.hasRelic("Runic Pyramid") && !AbstractDungeon.player.hasPower("Equilibrium")) {
                this.addToBot(new RetainCardsAction(this.owner, this.amount));
            }
        }
    }

    // =========================================================================
    // 核心逻辑：下回合开始时，释放存好的能量
    // =========================================================================
    @Override
    public void atStartOfTurn() {
        // 【核心修复】必须调用父类的方法！
        // 这样才能触发 AbstractDaoHenPower 中统一的“变回变化道道痕”的逻辑
        super.atStartOfTurn();

        if (this.energyRetained > 0) {
            this.flash();
            this.addToBot(new GainEnergyAction(this.energyRetained));
            this.energyRetained = 0;
        }
    }
}