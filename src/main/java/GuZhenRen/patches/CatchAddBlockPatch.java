package GuZhenRen.patches;

import GuZhenRen.cards.DiMai;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;

@SpirePatch(clz = AbstractCreature.class, method = "addBlock", paramtypez = {int.class})
public class CatchAddBlockPatch {

    @SpirePostfixPatch
    public static void Postfix(AbstractCreature __instance, int blockAmount) {
        if (__instance.isPlayer && blockAmount > 0) {
            DiMai.blockGainedCountThisCombat++;
        }
    }
}