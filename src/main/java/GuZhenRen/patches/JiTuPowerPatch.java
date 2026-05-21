package GuZhenRen.patches;

import GuZhenRen.powers.JiTuPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;

@SpirePatch(clz = AbstractCreature.class, method = "hasPower")
public class JiTuPowerPatch {

    @SpirePostfixPatch
    public static boolean Postfix(boolean __result, AbstractCreature __instance, String targetID) {
        if (!__result && "Barricade".equals(targetID) && __instance.hasPower(JiTuPower.POWER_ID)) {
            return true;
        }
        return __result;
    }
}