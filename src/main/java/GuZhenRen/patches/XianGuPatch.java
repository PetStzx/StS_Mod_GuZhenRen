package GuZhenRen.patches;

import GuZhenRen.GuZhenRen;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;

import java.util.ArrayList;

public class XianGuPatch {

    /**
     * 辅助方法：检查并移除重复的仙蛊
     * @param group 当前操作的牌组
     * @param addedCard 刚刚加入的卡牌
     */
    private static void checkAndRemoveDuplicate(CardGroup group, AbstractCard addedCard) {
        // 1. 基础资格检查
        if (!addedCard.hasTag(GuZhenRenTags.XIAN_GU)) return;
        if (addedCard.hasTag(GuZhenRenTags.BEN_MING_GU)) return;

        if (AbstractDungeon.player == null) return;

        // 2. 环境检查
        boolean isRelevantGroup = (group == AbstractDungeon.player.masterDeck) ||
                (group == AbstractDungeon.player.hand) ||
                (group == AbstractDungeon.player.drawPile) ||
                (group == AbstractDungeon.player.discardPile) ||
                (group == AbstractDungeon.player.exhaustPile) ||
                (group == AbstractDungeon.player.limbo);

        if (!isRelevantGroup) return;

        // 3. 查重逻辑
        boolean isDuplicate = false;

        if (group == AbstractDungeon.player.masterDeck) {
            // 永久获得检查
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c != addedCard && c.cardID.equals(addedCard.cardID) && c.hasTag(GuZhenRenTags.XIAN_GU)) {
                    isDuplicate = true;
                    break;
                }
            }
        } else {
            // 战斗内检查
            ArrayList<AbstractCard> allCombatCards = new ArrayList<>();
            allCombatCards.addAll(AbstractDungeon.player.hand.group);
            allCombatCards.addAll(AbstractDungeon.player.drawPile.group);
            allCombatCards.addAll(AbstractDungeon.player.discardPile.group);
            allCombatCards.addAll(AbstractDungeon.player.exhaustPile.group);
            allCombatCards.addAll(AbstractDungeon.player.limbo.group);

            for (AbstractCard c : allCombatCards) {
                if (c != addedCard && c.cardID.equals(addedCard.cardID) && c.hasTag(GuZhenRenTags.XIAN_GU)) {
                    isDuplicate = true;
                    break;
                }
            }
        }

        // 4. 执行移除与特效
        if (isDuplicate) {
            GuZhenRen.logger.info("发现重复仙蛊，执行消散: " + addedCard.name);

            // 【视觉修复】
            // 在生成特效前，强制将卡牌位置瞬移到玩家身体中心
            // 这样特效就会在玩家身上播放，而不是在半空中或者手牌栏里滑动
            addedCard.current_x = AbstractDungeon.player.hb.cX;
            addedCard.current_y = AbstractDungeon.player.hb.cY;
            addedCard.target_x = addedCard.current_x; // 锁定目标位置，防止引擎试图插值移动
            addedCard.target_y = addedCard.current_y;

            // 播放消耗音效
            CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);

            // 播放卡牌消耗动画
            // 此时卡牌坐标已被我们锁定在玩家身上，动画会原地播放，不会平移
            AbstractDungeon.topLevelEffectsQueue.add(new ExhaustCardEffect(addedCard));

            // 从当前组中移除它
            group.removeCard(addedCard);
        }
    }

    // --- Postfix 拦截 ---

    @SpirePatch(clz = CardGroup.class, method = "addToHand")
    public static class AddToHandPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            checkAndRemoveDuplicate(__instance, c);
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "addToTop")
    public static class AddToTopPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            checkAndRemoveDuplicate(__instance, c);
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "addToBottom")
    public static class AddToBottomPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            checkAndRemoveDuplicate(__instance, c);
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "addToRandomSpot")
    public static class AddToRandomSpotPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            checkAndRemoveDuplicate(__instance, c);
        }
    }
}