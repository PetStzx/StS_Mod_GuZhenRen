package GuZhenRen.patches;

import GuZhenRen.powers.QiHuPower;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class QiHuPatch {

    // 1. 拦截伤害
    @SpirePatch(clz = AbstractMonster.class, method = "damage")
    public static class DamageRedirectionPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractMonster __instance, DamageInfo info) {
            if (AbstractDungeon.getMonsters() != null) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.isDeadOrEscaped() && m.hasPower(QiHuPower.POWER_ID)) {
                        QiHuPower qm = (QiHuPower) m.getPower(QiHuPower.POWER_ID);
                        if (qm.protectedTarget == __instance && __instance != m) {
                            qm.flash();
                            CardCrawlGame.sound.play("ATTACK_WHIFF_2");
                            m.damage(info);
                            return SpireReturn.Return();
                        }
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }

    // 2. 拦截DEBUFF
    @SpirePatch(clz = ApplyPowerAction.class, method = "update")
    public static class ApplyPowerRedirectionPatch {
        @SpirePrefixPatch
        public static void Prefix(ApplyPowerAction __instance) {
            if (!__instance.isDone && __instance.target != null && __instance.target instanceof AbstractMonster) {
                AbstractPower powerToApply = (AbstractPower) ReflectionHacks.getPrivate(__instance, ApplyPowerAction.class, "powerToApply");
                if (powerToApply != null && powerToApply.type == AbstractPower.PowerType.DEBUFF) {
                    if (AbstractDungeon.getMonsters() != null) {
                        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                            if (!m.isDeadOrEscaped() && m.hasPower(QiHuPower.POWER_ID)) {
                                QiHuPower qm = (QiHuPower) m.getPower(QiHuPower.POWER_ID);
                                if (qm.protectedTarget == __instance.target && __instance.target != m) {
                                    qm.flash();
                                    CardCrawlGame.sound.play("ATTACK_WHIFF_2");
                                    __instance.target = m;
                                    powerToApply.owner = m;

                                    powerToApply.updateDescription();

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 3. 拦截InstantKillAction
    @SpirePatch(clz = InstantKillAction.class, method = "update")
    public static class InstantKillRedirectionPatch {
        @SpirePrefixPatch
        public static void Prefix(InstantKillAction __instance) {
            if (!__instance.isDone && __instance.target != null && __instance.target instanceof AbstractMonster) {
                if (AbstractDungeon.getMonsters() != null) {
                    for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                        if (!m.isDeadOrEscaped() && m.hasPower(QiHuPower.POWER_ID)) {
                            QiHuPower qm = (QiHuPower) m.getPower(QiHuPower.POWER_ID);
                            if (qm.protectedTarget == __instance.target && __instance.target != m) {
                                qm.flash();
                                CardCrawlGame.sound.play("ATTACK_WHIFF_2");
                                __instance.target = m;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}