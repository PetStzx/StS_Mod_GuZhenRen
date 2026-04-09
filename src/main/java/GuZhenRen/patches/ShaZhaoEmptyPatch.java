package GuZhenRen.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;

@SpirePatch(
        clz = GridCardSelectScreen.class,
        method = "updateControllerInput"
)
public class ShaZhaoEmptyPatch {

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(GridCardSelectScreen __instance) {
        // 当选牌界面被打开，且里面的卡牌数量为 0 时
        if (__instance.targetGroup != null && __instance.targetGroup.isEmpty()) {
            // 阻断原版的手柄光标更新逻辑，防止 Index: 0, Size: 0 的崩溃
            return SpireReturn.Return();
        }

        // 如果有牌，则执行原版逻辑
        return SpireReturn.Continue();
    }
}