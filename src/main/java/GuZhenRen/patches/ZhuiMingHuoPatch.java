package GuZhenRen.patches;

import GuZhenRen.powers.ZhuiMingHuoPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ZhuiMingHuoPatch {

    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction"
    )
    public static class SpreadFirePatch {

        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractMonster.class.getName()) && m.getMethodName().equals("takeTurn")) {
                        m.replace("$_ = $proceed($$); GuZhenRen.patches.ZhuiMingHuoPatch.SpreadFirePatch.postfixLogic($0);");
                    }
                }
            };
        }

        public static void postfixLogic(AbstractMonster __instance) {
            // 1. 判断该怪物是否执行了攻击意图
            boolean isAttacking = isAttackingIntent(__instance.intent);

            if (isAttacking) {
                // 2. 检查场上是否有任意单位拥有追命火
                boolean sourceExists = false;
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped() && m.hasPower(ZhuiMingHuoPower.POWER_ID)) {
                        sourceExists = true;
                        break;
                    }
                }

                // 3. 如果场上有追命火，且自己进行了攻击，则感染
                if (sourceExists) {
                    AbstractDungeon.actionManager.addToBottom(
                            new ZhuiMingHuoPower.ZhuiMingHuoSpreadAction(__instance, 1)
                    );
                }
            }
        }

        // 判断意图是否为攻击
        private static boolean isAttackingIntent(AbstractMonster.Intent intent) {
            return intent == AbstractMonster.Intent.ATTACK ||
                    intent == AbstractMonster.Intent.ATTACK_BUFF ||
                    intent == AbstractMonster.Intent.ATTACK_DEBUFF ||
                    intent == AbstractMonster.Intent.ATTACK_DEFEND;
        }
    }
}