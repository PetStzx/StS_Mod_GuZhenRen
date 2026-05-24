package GuZhenRen.patches;

import GuZhenRen.relics.FeiLiGu;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

@SpirePatch(clz = AbstractPower.class, method = "atEndOfTurn", paramtypez = {boolean.class})
public class FeiLiGuPatch {

    @SpirePrefixPatch
    public static void Prefix(AbstractPower __instance, boolean isPlayer) {
        if (!isPlayer && StrengthPower.POWER_ID.equals(__instance.ID) && __instance.amount > 0) {

            if (!__instance.owner.isDeadOrEscaped() && AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(FeiLiGu.ID)) {

                AbstractDungeon.player.getRelic(FeiLiGu.ID).flash();

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(
                        __instance.owner,
                        AbstractDungeon.player,
                        new StrengthPower(__instance.owner, -1),
                        -1
                ));
            }
        }
    }
}