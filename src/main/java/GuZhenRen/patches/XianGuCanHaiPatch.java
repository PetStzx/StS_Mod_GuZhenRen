package GuZhenRen.patches;

import GuZhenRen.relics.XianGuCanHai;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.DigOption;
import com.megacrit.cardcrawl.ui.campfire.LiftOption;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import com.megacrit.cardcrawl.ui.campfire.SmithOption;
import com.megacrit.cardcrawl.ui.campfire.TokeOption;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSmithEffect;

import java.util.ArrayList;

@SpirePatch(clz = CampfireSmithEffect.class, method = "update")
public class XianGuCanHaiPatch {
    public static boolean isLocalAction = false;
    public static boolean cardWasSelected = false;

    @SpirePrefixPatch
    public static void Prefix(CampfireSmithEffect __instance) {
        if (AbstractDungeon.gridSelectScreen != null && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            cardWasSelected = true;
        }
    }

    @SpirePostfixPatch
    public static void Postfix(CampfireSmithEffect __instance) {
        if (__instance.isDone) {
            if (isLocalAction && cardWasSelected && AbstractDungeon.player.hasRelic(XianGuCanHai.ID)) {
                XianGuCanHai relic = (XianGuCanHai) AbstractDungeon.player.getRelic(XianGuCanHai.ID);

                if (relic.counter > 0) {

                    boolean hasUpgradable = false;
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (c.canUpgrade()) {
                            hasUpgradable = true;
                            break;
                        }
                    }

                    boolean hasActionEndingOptions = false;
                    if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
                        RestRoom room = (RestRoom) AbstractDungeon.getCurrRoom();

                        @SuppressWarnings("unchecked")
                        ArrayList<AbstractCampfireOption> buttons = (ArrayList<AbstractCampfireOption>) ReflectionHacks.getPrivate(
                                room.campfireUI,
                                CampfireUI.class,
                                "buttons"
                        );

                        for (AbstractCampfireOption option : buttons) {
                            if (option instanceof SmithOption) {
                                option.usable = hasUpgradable;
                            }

                            // 白名单
                            if (option.usable) {
                                if (option instanceof RestOption ||
                                        option instanceof SmithOption ||
                                        option instanceof TokeOption ||
                                        option instanceof DigOption ||
                                        option instanceof LiftOption) {

                                    hasActionEndingOptions = true;
                                }
                            }
                        }

                        if (hasActionEndingOptions) {
                            relic.useCharge();
                            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
                            room.campfireUI.reopen();
                        }
                    }
                }
            }
            isLocalAction = false;
            cardWasSelected = false;
        }
    }
}