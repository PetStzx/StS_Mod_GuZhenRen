package GuZhenRen.patches;

import GuZhenRen.powers.ZhuiMingHuoPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager; // 修改 Patch 目标为 GameActionManager
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
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

        // 使用 Instrument 方式，修改字节码中的方法调用
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    // 找到代码中调用 monster.takeTurn() 的地方
                    if (m.getClassName().equals(AbstractMonster.class.getName()) && m.getMethodName().equals("takeTurn")) {

                        // 将原来的调用替换为：先执行原调用($proceed)，然后执行我们的逻辑(postfixLogic)
                        // $0 代表调用该方法的对象（即当前的 AbstractMonster）
                        m.replace("$_ = $proceed($$); GuZhenRen.patches.ZhuiMingHuoPatch.SpreadFirePatch.postfixLogic($0);");
                    }
                }
            };
        }

        // 这是被注入的逻辑，会在 monster.takeTurn() 执行完后立即运行
        // 此时怪物刚刚把攻击动作加入队列，我们使用 addToBottom 就能确保追命火在攻击后生效
        public static void postfixLogic(AbstractMonster __instance) {
            // 1. 判断该怪物是否执行了攻击意图
            boolean isAttacking = isAttackingIntent(__instance.intent);

            if (isAttacking) {
                // 2. 检查场上是否有“传染源” (是否有任意单位拥有追命火)
                boolean sourceExists = false;
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!m.isDeadOrEscaped() && m.hasPower(ZhuiMingHuoPower.POWER_ID)) {
                        sourceExists = true;
                        break;
                    }
                }

                // 3. 如果场上有追命火，且自己进行了攻击，则感染/加深
                if (sourceExists) {
                    // 使用 addToBottom 确保在当前所有结算之后发生
                    AbstractDungeon.actionManager.addToBottom(
                            new ApplyPowerAction(__instance, __instance,
                                    new ZhuiMingHuoPower(__instance, 1), 1));
                }
            }
        }

        // 辅助方法：判断意图是否为攻击
        private static boolean isAttackingIntent(AbstractMonster.Intent intent) {
            return intent == AbstractMonster.Intent.ATTACK ||
                    intent == AbstractMonster.Intent.ATTACK_BUFF ||
                    intent == AbstractMonster.Intent.ATTACK_DEBUFF ||
                    intent == AbstractMonster.Intent.ATTACK_DEFEND;
        }
    }
}