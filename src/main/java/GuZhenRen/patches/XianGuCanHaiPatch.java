package GuZhenRen.patches;

import GuZhenRen.relics.XianGuCanHai;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSmithEffect;

@SpirePatch(clz = CampfireSmithEffect.class, method = "update")
public class XianGuCanHaiPatch {

    @SpirePostfixPatch
    public static void Postfix(CampfireSmithEffect __instance) {
        // 当一次锻造动作执行完毕时触发
        if (__instance.isDone) {
            if (AbstractDungeon.player.hasRelic(XianGuCanHai.ID)) {
                XianGuCanHai relic = (XianGuCanHai) AbstractDungeon.player.getRelic(XianGuCanHai.ID);

                // 如果遗物依然有可用层数
                if (relic.counter > 0) {
                    relic.useCharge(); // 扣除1次免费额度

                    // 1. 将房间状态从“已完成”强行重置为“未完成”
                    AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;

                    // 2. 重新唤醒篝火UI界面，让玩家可以继续行动
                    if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
                        ((RestRoom) AbstractDungeon.getCurrRoom()).campfireUI.reopen();
                    }
                }
            }
        }
    }
}