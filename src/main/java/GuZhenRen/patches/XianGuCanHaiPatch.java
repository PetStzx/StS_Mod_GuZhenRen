package GuZhenRen.patches;

import GuZhenRen.relics.XianGuCanHai;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSmithEffect;

@SpirePatch(clz = CampfireSmithEffect.class, method = "update")
public class XianGuCanHaiPatch {
    // 标记1：判断是否是本地玩家点击的（防联机错乱）
    public static boolean isLocalAction = false;

    // 标记2：判断玩家是否真的选中了卡牌
    public static boolean cardWasSelected = false;

    @SpirePrefixPatch
    public static void Prefix(CampfireSmithEffect __instance) {
        // 在原版 update 逻辑执行之前，检查选牌列表是否非空
        if (AbstractDungeon.gridSelectScreen != null && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            cardWasSelected = true;
        }
    }

    @SpirePostfixPatch
    public static void Postfix(CampfireSmithEffect __instance) {
        if (__instance.isDone) {
            // 同时满足 本地点击 且 真的选了牌，才消耗遗物层数
            if (isLocalAction && cardWasSelected && AbstractDungeon.player.hasRelic(XianGuCanHai.ID)) {
                XianGuCanHai relic = (XianGuCanHai) AbstractDungeon.player.getRelic(XianGuCanHai.ID);

                if (relic.counter > 0) {
                    relic.useCharge();
                    AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
                    if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
                        ((RestRoom) AbstractDungeon.getCurrRoom()).campfireUI.reopen();
                    }
                }
            }

            // 动作结束后，关锁
            isLocalAction = false;
            cardWasSelected = false;
        }
    }
}