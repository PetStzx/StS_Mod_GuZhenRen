package GuZhenRen.patches;

import GuZhenRen.cards.DiMai;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

import java.util.ArrayList;

public class DiMaiBottomPatch {

    public static void sinkDiMai(CardGroup group) {
        if (group.type == CardGroup.CardGroupType.DRAW_PILE && !group.isEmpty()) {
            ArrayList<AbstractCard> dimais = new ArrayList<>();
            for (AbstractCard c : group.group) {
                if (c instanceof DiMai) {
                    dimais.add(c);
                }
            }

            if (!dimais.isEmpty() && dimais.size() < group.size()) {
                group.group.removeAll(dimais);
                group.group.addAll(0, dimais);
            }
        }
    }

    // ==========================================================
    // 拦截所有改变抽牌堆的操作
    // ==========================================================

    // 1：带随机数的洗牌（如战斗开始、弃牌堆洗入）
    @SpirePatch(clz = CardGroup.class, method = "shuffle", paramtypez = {com.megacrit.cardcrawl.random.Random.class})
    public static class ShufflePatch1 {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance) {
            sinkDiMai(__instance);
        }
    }

    // 2：无参数洗牌
    @SpirePatch(clz = CardGroup.class, method = "shuffle", paramtypez = {})
    public static class ShufflePatch2 {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance) {
            sinkDiMai(__instance);
        }
    }

    // 3：加入顶部
    @SpirePatch(clz = CardGroup.class, method = "addToTop")
    public static class AddToTopPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            sinkDiMai(__instance);
        }
    }

    // 4：随机加入
    @SpirePatch(clz = CardGroup.class, method = "addToRandomSpot")
    public static class AddToRandomSpotPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            sinkDiMai(__instance);
        }
    }

    // 5：加入底部
    @SpirePatch(clz = CardGroup.class, method = "addToBottom")
    public static class AddToBottomPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            sinkDiMai(__instance);
        }
    }
}