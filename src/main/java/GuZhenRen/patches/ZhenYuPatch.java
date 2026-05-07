package GuZhenRen.patches;

import GuZhenRen.powers.ZhenYuPower;
import GuZhenRen.util.BattleStateManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhenYuPatch {
    public static boolean isTurnStartDrawPhase = false;

    static {
        BattleStateManager.onBattleStart(() -> ZhenYuPatch.isTurnStartDrawPhase = false);
        BattleStateManager.onPostBattle(() -> ZhenYuPatch.isTurnStartDrawPhase = false);
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
    public static class StartPhase {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance) {
            isTurnStartDrawPhase = true;
        }
    }

    @SpirePatch(clz = com.megacrit.cardcrawl.actions.common.EnableEndTurnButtonAction.class, method = "update")
    public static class EndPhase {
        @SpirePrefixPatch
        public static void Prefix(com.megacrit.cardcrawl.actions.common.EnableEndTurnButtonAction __instance) {
            isTurnStartDrawPhase = false;
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "draw", paramtypez = {int.class})
    public static class DrawIntercept {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractPlayer __instance, int numCards) {
            if (isTurnStartDrawPhase) {
                return SpireReturn.Continue();
            }

            if (AbstractDungeon.getMonsters() != null) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.isDeadOrEscaped() && m.hasPower(ZhenYuPower.POWER_ID)) {
                        m.getPower(ZhenYuPower.POWER_ID).flash();
                        com.megacrit.cardcrawl.actions.common.DrawCardAction.drawnCards.clear();

                        return SpireReturn.Return(null);
                    }
                }
            }

            return SpireReturn.Continue();
        }
    }
}