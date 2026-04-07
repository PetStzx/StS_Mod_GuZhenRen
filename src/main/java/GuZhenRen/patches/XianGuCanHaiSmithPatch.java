package GuZhenRen.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.ui.campfire.SmithOption;

@SpirePatch(clz = SmithOption.class, method = "useOption")
public class XianGuCanHaiSmithPatch {
    @SpirePrefixPatch
    public static void Prefix(SmithOption __instance) {
        // 只有当你点击了这个按钮，才会把标记设为 true
        XianGuCanHaiPatch.isLocalAction = true;
    }
}