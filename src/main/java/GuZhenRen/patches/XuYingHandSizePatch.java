package GuZhenRen.patches;

import GuZhenRen.cards.AbstractXuYingCard;
import GuZhenRen.powers.LiQiPower;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(clz = MakeTempCardInHandAction.class, method = "update")
public class XuYingHandSizePatch {

    private static boolean isBoosted = false;

    @SpirePrefixPatch
    public static void Prefix(MakeTempCardInHandAction __instance) {
        isBoosted = false;

        if (__instance.isDone) return;

        if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(LiQiPower.POWER_ID)) {
            if (__instance.amount > 0) {
                AbstractCard c = ReflectionHacks.getPrivate(__instance, MakeTempCardInHandAction.class, "c");

                if (c instanceof AbstractXuYingCard) {
                    BaseMod.MAX_HAND_SIZE += 99;
                    isBoosted = true;
                }
            }
        }
    }

    @SpirePostfixPatch
    public static void Postfix(MakeTempCardInHandAction __instance) {
        if (isBoosted) {
            BaseMod.MAX_HAND_SIZE -= 99;
            isBoosted = false;
        }
    }
}