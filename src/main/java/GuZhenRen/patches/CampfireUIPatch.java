package GuZhenRen.patches;

import GuZhenRen.character.FangYuan;
import GuZhenRen.ui.AssembleShaZhaoOption;
import GuZhenRen.util.ShaZhaoHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import java.util.ArrayList;

@SpirePatch(clz = CampfireUI.class, method = "initializeButtons")
public class CampfireUIPatch {
    public static void Postfix(CampfireUI __instance, ArrayList<AbstractCampfireOption> ___buttons) {

        // 检查当前角色是否为“古月方源”
        if (AbstractDungeon.player instanceof FangYuan) {

            // 检查是否有可合成的配方
            boolean canCraft = !ShaZhaoHelper.getCraftableRecipes().isEmpty();

            // 角色是方源时，添加“组并杀招”按钮
            ___buttons.add(new AssembleShaZhaoOption(canCraft));
        }
    }
}