package GuZhenRen.patches;

import GuZhenRen.relics.GuiBuJue;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class GuiBuJuePatch {

    public static boolean isGhost(String id) {
        if (id == null) return false;
        return id.equals("Hexaghost") ||
                id.equals("TheCollector") ||
                id.equals("TorchHead") ||
                id.equals("Transient") ||
                id.equals("Nemesis");
    }


    @SpirePatch(clz = AbstractGameAction.class, method = SpirePatch.CLASS)
    public static class GhostActionField {
        public static SpireField<Boolean> isGhostAction = new SpireField<>(() -> false);
    }

    @SpirePatches({
            @SpirePatch(clz = com.megacrit.cardcrawl.monsters.exordium.Hexaghost.class, method = "takeTurn"),
            @SpirePatch(clz = com.megacrit.cardcrawl.monsters.city.TheCollector.class, method = "takeTurn"),
            @SpirePatch(clz = com.megacrit.cardcrawl.monsters.city.TorchHead.class, method = "takeTurn"),
            @SpirePatch(clz = com.megacrit.cardcrawl.monsters.beyond.Transient.class, method = "takeTurn"),
            @SpirePatch(clz = com.megacrit.cardcrawl.monsters.beyond.Nemesis.class, method = "takeTurn")
    })
    public static class MonsterTurnTracker {
        public static AbstractMonster actingMonster = null;

        @SpirePrefixPatch
        public static void Prefix(AbstractMonster __instance) {
            actingMonster = __instance;
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractMonster __instance) {
            actingMonster = null;
        }
    }

    @SpirePatch(clz = AbstractGameAction.class, method = SpirePatch.CONSTRUCTOR)
    public static class ActionConstructorPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractGameAction __instance) {
            if (MonsterTurnTracker.actingMonster != null && isGhost(MonsterTurnTracker.actingMonster.id)) {
                GhostActionField.isGhostAction.set(__instance, true);
            }
        }
    }


    @SpirePatch(clz = com.megacrit.cardcrawl.actions.common.DamageAction.class, method = "update")
    public static class BlockDamageActionPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(com.megacrit.cardcrawl.actions.common.DamageAction __instance) {
            if (__instance.target == AbstractDungeon.player) {
                DamageInfo info = (DamageInfo) ReflectionHacks.getPrivate(__instance, com.megacrit.cardcrawl.actions.common.DamageAction.class, "info");
                if (info != null && info.owner instanceof AbstractMonster) {
                    if (AbstractDungeon.player.hasRelic(GuiBuJue.ID) && isGhost(((AbstractMonster) info.owner).id)) {
                        AbstractDungeon.player.getRelic(GuiBuJue.ID).flash();
                        __instance.isDone = true;
                        return SpireReturn.Return();
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = com.megacrit.cardcrawl.characters.AbstractPlayer.class, method = "damage")
    public static class BlockDamagePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(com.megacrit.cardcrawl.characters.AbstractPlayer __instance, DamageInfo info) {
            if (info.owner instanceof AbstractMonster) {
                if (__instance.hasRelic(GuiBuJue.ID) && isGhost(((AbstractMonster)info.owner).id)) {
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(clz = com.megacrit.cardcrawl.actions.animations.VFXAction.class, method = "update")
    public static class BlockCollectorCurseVFX {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(com.megacrit.cardcrawl.actions.animations.VFXAction __instance) {
            if (AbstractDungeon.player.hasRelic(GuiBuJue.ID) && GhostActionField.isGhostAction.get(__instance)) {
                com.megacrit.cardcrawl.vfx.AbstractGameEffect effect =
                        (com.megacrit.cardcrawl.vfx.AbstractGameEffect) ReflectionHacks.getPrivate(__instance, com.megacrit.cardcrawl.actions.animations.VFXAction.class, "effect");
                if (effect != null && "CollectorCurseEffect".equals(effect.getClass().getSimpleName())) {
                    __instance.isDone = true;
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(clz = com.megacrit.cardcrawl.actions.utility.SFXAction.class, method = "update")
    public static class BlockCollectorSFX {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(com.megacrit.cardcrawl.actions.utility.SFXAction __instance) {
            String key = (String) ReflectionHacks.getPrivate(__instance, com.megacrit.cardcrawl.actions.utility.SFXAction.class, "key");

            if ("MONSTER_COLLECTOR_DEBUFF".equals(key) && AbstractDungeon.player.hasRelic(GuiBuJue.ID) && GhostActionField.isGhostAction.get(__instance)) {
                __instance.isDone = true;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = com.megacrit.cardcrawl.actions.common.ApplyPowerAction.class, method = "update")
    public static class BlockDebuffPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(com.megacrit.cardcrawl.actions.common.ApplyPowerAction __instance) {
            if (__instance.target == AbstractDungeon.player && __instance.source instanceof AbstractMonster) {
                if (AbstractDungeon.player.hasRelic(GuiBuJue.ID) && isGhost(((AbstractMonster)__instance.source).id)) {
                    AbstractPower power = (AbstractPower) ReflectionHacks.getPrivate(__instance, com.megacrit.cardcrawl.actions.common.ApplyPowerAction.class, "powerToApply");
                    if (power != null && power.type == AbstractPower.PowerType.DEBUFF) {
                        AbstractDungeon.player.getRelic(GuiBuJue.ID).flash();
                        __instance.isDone = true;
                        return SpireReturn.Return();
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(clz = com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction.class, method = "update")
    public static class BlockDrawPileCardPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction __instance) {
            if (AbstractDungeon.player.hasRelic(GuiBuJue.ID) && GhostActionField.isGhostAction.get(__instance)) {
                AbstractDungeon.player.getRelic(GuiBuJue.ID).flash();
                __instance.isDone = true;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction.class, method = "update")
    public static class BlockDiscardCardPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction __instance) {
            if (AbstractDungeon.player.hasRelic(GuiBuJue.ID) && GhostActionField.isGhostAction.get(__instance)) {
                AbstractDungeon.player.getRelic(GuiBuJue.ID).flash();
                __instance.isDone = true;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}