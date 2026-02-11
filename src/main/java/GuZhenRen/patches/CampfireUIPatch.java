package GuZhenRen.patches;

import GuZhenRen.ui.AssembleShaZhaoOption;
import GuZhenRen.util.ShaZhaoHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import java.util.ArrayList;

@SpirePatch(clz = CampfireUI.class, method = "initializeButtons")
public class CampfireUIPatch {
    public static void Postfix(CampfireUI __instance, ArrayList<AbstractCampfireOption> ___buttons) {
        // 检查是否有可合成的配方
        boolean canCraft = !ShaZhaoHelper.getCraftableRecipes().isEmpty();

        // 添加按钮 (如果没有配方也显示按钮，但是是灰色的，提示玩家有这个功能)
        // 如果你希望没配方就不显示按钮，可以在这里加 if (canCraft) 判断
        ___buttons.add(new AssembleShaZhaoOption(canCraft));
    }
}