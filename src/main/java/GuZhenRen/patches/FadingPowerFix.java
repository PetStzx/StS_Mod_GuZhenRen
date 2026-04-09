package GuZhenRen.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.beyond.AwakenedOne;
import com.megacrit.cardcrawl.powers.FadingPower;

@SpirePatch(
        clz = FadingPower.class,
        method = "duringTurn"
)
public class FadingPowerFix {
    @SpirePrefixPatch
    public static void Prefix(FadingPower __instance) {
        if (__instance.amount == 1 && !__instance.owner.isDying) {
            if (__instance.owner instanceof AwakenedOne) {
                AbstractDungeon.getCurrRoom().cannotLose = false;
            }
        }
    }
}