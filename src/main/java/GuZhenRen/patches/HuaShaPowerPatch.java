package GuZhenRen.patches;

import GuZhenRen.powers.HuaShaPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class HuaShaPowerPatch {

    public static boolean isDamageProcessing = false;
    public static int damageBlockBefore = 0;
    public static int loseBlockBefore = 0;

    @SpirePatch(clz = AbstractPlayer.class, method = "damage", paramtypez = {DamageInfo.class})
    public static class CatchDamagePatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance, DamageInfo info) {
            damageBlockBefore = __instance.currentBlock;
            isDamageProcessing = true;
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance, DamageInfo info) {
            isDamageProcessing = false;
            int lost = damageBlockBefore - __instance.currentBlock;

            if (lost > 0 && __instance.hasPower(HuaShaPower.POWER_ID)) {
                HuaShaPower power = (HuaShaPower) __instance.getPower(HuaShaPower.POWER_ID);
                power.triggerHuaSha(lost);
            }
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "loseBlock", paramtypez = {int.class, boolean.class})
    public static class CatchLoseBlockPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature __instance, int amount, boolean noAnimation) {
            loseBlockBefore = __instance.currentBlock;
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractCreature __instance, int amount, boolean noAnimation) {
            if (!isDamageProcessing) {
                int lost = loseBlockBefore - __instance.currentBlock;
                if (lost > 0 && __instance.isPlayer && __instance.hasPower(HuaShaPower.POWER_ID)) {
                    HuaShaPower power = (HuaShaPower) __instance.getPower(HuaShaPower.POWER_ID);
                    power.triggerHuaSha(lost);
                }
            }
        }
    }
}