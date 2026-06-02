package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.AbstractDaoHenPower;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.HashSet;
import java.util.WeakHashMap;

public class JianChiGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("JianChiGu");
    private static final String IMG = GuZhenRen.assetPath("img/relics/JianChiGu.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/JianChiGu.png");

    public HashSet<AbstractPower> perseveredPowers = new HashSet<>();
    public static WeakHashMap<AbstractGameAction, Boolean> expirationTags = new WeakHashMap<>();

    public JianChiGu() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void atPreBattle() {
        this.perseveredPowers.clear();
        expirationTags.clear();
    }

    @Override
    public AbstractRelic makeCopy() {
        return new JianChiGu();
    }


    public static void tagExpirationAction(AbstractGameAction action) {
        if (action instanceof RemoveSpecificPowerAction || action instanceof ReducePowerAction) {

            boolean isCollateral = false;
            boolean isNatural = false;

            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                String methodName = element.getMethodName();

                if (methodName.equals("onRemove") || methodName.equals("onSpecificTrigger")) {
                    isCollateral = true;
                    break;
                }

                if (methodName.equals("atEndOfTurn") ||
                        methodName.equals("atEndOfRound") ||
                        methodName.equals("atStartOfTurn") ||
                        methodName.equals("atStartOfTurnPostDraw") ||
                        methodName.equals("atEndOfTurnPreEndTurnCards")) {
                    isNatural = true;
                }
            }

            if (isCollateral) {
                return;
            }

            if (isNatural) {
                expirationTags.put(action, true);
                return;
            }

            if (AbstractDungeon.actionManager != null && AbstractDungeon.actionManager.currentAction != null) {
                if (expirationTags.getOrDefault(AbstractDungeon.actionManager.currentAction, false)) {
                    expirationTags.put(action, true);
                }
            }
        }
    }

    @SpirePatch(clz = GameActionManager.class, method = "addToBottom")
    public static class AddToBottomPatch {
        @SpirePrefixPatch
        public static void Prefix(GameActionManager __instance, AbstractGameAction action) {
            tagExpirationAction(action);
        }
    }

    @SpirePatch(clz = GameActionManager.class, method = "addToTop")
    public static class AddToTopPatch {
        @SpirePrefixPatch
        public static void Prefix(GameActionManager __instance, AbstractGameAction action) {
            tagExpirationAction(action);
        }
    }

    @SpirePatch(clz = RemoveSpecificPowerAction.class, method = "update")
    public static class RemovePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(RemoveSpecificPowerAction __instance) {

            if (!expirationTags.getOrDefault(__instance, false)) {
                return SpireReturn.Continue();
            }

            if (__instance.target != null && __instance.target == AbstractDungeon.player) {
                AbstractPower p = ReflectionHacks.getPrivate(__instance, RemoveSpecificPowerAction.class, "powerInstance");
                if (p == null) {
                    String powerID = ReflectionHacks.getPrivate(__instance, RemoveSpecificPowerAction.class, "powerToRemove");
                    if (powerID != null) {
                        p = __instance.target.getPower(powerID);
                    }
                }

                if (p != null && p.type == AbstractPower.PowerType.BUFF) {

                    if (p instanceof AbstractDaoHenPower) {
                        return SpireReturn.Continue();
                    }

                    if (AbstractDungeon.player.hasRelic(JianChiGu.ID)) {
                        JianChiGu relic = (JianChiGu) AbstractDungeon.player.getRelic(JianChiGu.ID);

                        if (!relic.perseveredPowers.contains(p)) {

                            relic.perseveredPowers.add(p);
                            relic.flash();

                            if (p.amount == 0) {
                                p.amount = 1;
                            }
                            p.updateDescription();

                            __instance.isDone = true;
                            return SpireReturn.Return();
                        }
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}