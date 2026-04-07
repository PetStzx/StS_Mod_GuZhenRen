package GuZhenRen.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class YuHuoPatch {
    // 静态全局变量，用于记录本回合消耗的卡牌数
    public static int cardsExhaustedThisTurn = 0;

    // 1：监听卡牌进入消耗堆的动作
    @SpirePatch(clz = CardGroup.class, method = "moveToExhaustPile")
    public static class CountExhaustPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            cardsExhaustedThisTurn++;
        }
    }

    // 2：玩家回合开始时，清零计数器
    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnCards")
    public static class ResetExhaustTurnPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            cardsExhaustedThisTurn = 0;
        }
    }

    // 3：战斗开始时，清零计数器
    @SpirePatch(clz = AbstractPlayer.class, method = "applyPreCombatLogic")
    public static class ResetExhaustBattlePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            cardsExhaustedThisTurn = 0;
        }
    }
}