package GuZhenRen.patches;

import GuZhenRen.powers.TunHuoPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TunHuoPatch {

    // 补丁：修改卡牌是否可用的判定 (AbstractCard.canUse)
    @SpirePatch(
            clz = AbstractCard.class,
            method = "canUse"
    )
    public static class AllowPlayingBurnPatch {
        // Postfix: 在原版判断结束后执行
        // __result: 原版判断的结果 (能不能打出)
        // __instance: 当前这张卡牌
        public static boolean Postfix(boolean __result, AbstractCard __instance, AbstractPlayer p, AbstractMonster m) {
            // 如果原版判定为不可用 (false)
            if (!__result) {
                // 1. 检查这张牌是不是“灼伤”
                // 2. 检查玩家是否有“吞火”能力
                if (__instance.cardID.equals(Burn.ID) && p.hasPower(TunHuoPower.POWER_ID)) {
                    // 强制允许打出
                    return true;
                }
            }
            // 否则保持原结果
            return __result;
        }
    }
}