package GuZhenRen.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@SpirePatch(clz = AbstractPlayer.class, method = "preBattlePrep")
public class CombatStaticCleanupPatch {

    @SpirePrefixPatch
    public static void Prefix(AbstractPlayer __instance) {
        GuZhenRen.powers.RuiYiPower.isActive = false;
    }
}