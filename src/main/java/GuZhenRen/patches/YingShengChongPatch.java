package GuZhenRen.patches;

import GuZhenRen.powers.YingShengChongPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.EnableEndTurnButtonAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

@SpirePatch(clz = EnableEndTurnButtonAction.class, method = "update")
public class YingShengChongPatch {

    @SpirePrefixPatch
    public static void Prefix(EnableEndTurnButtonAction __instance) {
        if (!__instance.isDone) {
            if (AbstractDungeon.getMonsters() != null) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.isDeadOrEscaped()) {
                        AbstractPower p = m.getPower(YingShengChongPower.POWER_ID);
                        if (p instanceof YingShengChongPower) {
                            ((YingShengChongPower) p).triggerLockCard();
                        }
                    }
                }
            }
        }
    }
}