package GuZhenRen.patches;

import GuZhenRen.relics.CunGuangYin;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSmithEffect;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CunGuangYinPatch {

    public static int extraUpgrades = 0;
    public static boolean isExtraUpgrading = false;

    @SpirePatch(clz = CampfireSmithEffect.class, method = SpirePatch.CONSTRUCTOR)
    public static class InitPatch {
        @SpirePostfixPatch
        public static void Postfix(CampfireSmithEffect __instance) {
            extraUpgrades = 0;
            isExtraUpgrading = false;
            if (AbstractDungeon.player.hasRelic(CunGuangYin.ID)) {
                extraUpgrades += 1;
            }
        }
    }

    @SpirePatch(clz = CampfireSmithEffect.class, method = "update")
    public static class UpdateInsertPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(CampfireSmithEffect __instance) {

            if (extraUpgrades > 0 && !AbstractDungeon.player.masterDeck.getUpgradableCards().isEmpty()) {
                extraUpgrades--;
                isExtraUpgrading = true;

                if (AbstractDungeon.player.hasRelic(CunGuangYin.ID)) {
                    AbstractDungeon.player.getRelic(CunGuangYin.ID).flash();
                }

                ReflectionHacks.setPrivate(__instance, CampfireSmithEffect.class, "openedScreen", false);
                ReflectionHacks.setPrivate(__instance, CampfireSmithEffect.class, "selectedCard", false);
                ReflectionHacks.setPrivate(__instance, CampfireSmithEffect.class, "screenColor", AbstractDungeon.fadeColor.cpy());
                ReflectionHacks.setPrivate(__instance, CampfireSmithEffect.class, "duration", 1.5F);
            } else {
                isExtraUpgrading = false;
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "clear");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = CampfireSmithEffect.class, method = "update")
    public static class HideCancelButtonPatch {
        @SpirePostfixPatch
        public static void Postfix(CampfireSmithEffect __instance) {
            if (isExtraUpgrading && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
                AbstractDungeon.overlayMenu.cancelButton.hide();
                ReflectionHacks.setPrivate(AbstractDungeon.gridSelectScreen, GridCardSelectScreen.class, "canCancel", false);
            }
        }
    }
}