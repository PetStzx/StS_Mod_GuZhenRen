package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.BattleStateManager;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;

public class TouDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("TouDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    // 全局静态变量：记录本场战斗偷取的总金币
    public static int totalGoldStolenThisCombat = 0;
    private static final int MAX_GOLD_PER_COMBAT = 30;

    static {
        BattleStateManager.onBattleStart(() -> TouDaoDaoHenPower.totalGoldStolenThisCombat = 0);
        BattleStateManager.onPostBattle(() -> TouDaoDaoHenPower.totalGoldStolenThisCombat = 0);
    }

    public TouDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        int remainingGold = MAX_GOLD_PER_COMBAT - totalGoldStolenThisCombat;
        if (remainingGold < 0) remainingGold = 0;

        this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1] + remainingGold + powerStrings.DESCRIPTIONS[2];
    }

    // =========================================================================
    // 造成攻击伤害时偷钱
    // =========================================================================
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        // 普通攻击伤害且目标不是自己
        if (target != this.owner && info.type == DamageInfo.DamageType.NORMAL) {

            int remainingGold = MAX_GOLD_PER_COMBAT - totalGoldStolenThisCombat;

            if (remainingGold > 0) {
                this.flash();

                // 计算本次实际能偷到的金币（不超过 30 上限）
                int goldToSteal = Math.min(this.amount, remainingGold);

                // 给玩家加钱
                AbstractDungeon.player.gainGold(goldToSteal);

                // 播放特效
                int effectCount = Math.min(goldToSteal, 5);
                for (int i = 0; i < effectCount; i++) {
                    AbstractDungeon.effectList.add(new GainPennyEffect(target.hb.cX, target.hb.cY));
                }

                // 累加到全局统计变量中
                TouDaoDaoHenPower.totalGoldStolenThisCombat += goldToSteal;

                // 刷新描述
                this.updateDescription();
            }
        }
    }
}