package GuZhenRen.patches;

import GuZhenRen.GuZhenRen;
import GuZhenRen.relics.XianGuCanHai;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;

import java.util.ArrayList;

public class XianGuPatch {

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("ActionUI"));
    public static final String[] TEXT = uiStrings.TEXT;

    /**
     * 辅助方法：检查并移除重复的仙蛊
     * @param group 当前操作的牌组
     * @param addedCard 刚刚加入的卡牌
     */
    private static void checkAndRemoveDuplicate(CardGroup group, AbstractCard addedCard) {
        // 1. 基础资格检查：如果刚加入的牌是虚影，直接放行
        if (!addedCard.hasTag(GuZhenRenTags.XIAN_GU)) return;
        if (addedCard.hasTag(GuZhenRenTags.BEN_MING_GU)) return;
        if (addedCard.hasTag(GuZhenRenTags.XU_YING_COPY)) return;
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
                // 查重时，无视带有虚影标签的牌
                if (c != addedCard && c.cardID.equals(addedCard.cardID) && c.hasTag(GuZhenRenTags.XIAN_GU) && !c.hasTag(GuZhenRenTags.XU_YING_COPY)) {
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
                // 同上，扫描时忽略虚影牌
                if (c != addedCard && c.cardID.equals(addedCard.cardID) && c.hasTag(GuZhenRenTags.XIAN_GU) && !c.hasTag(GuZhenRenTags.XU_YING_COPY)) {
                    isDuplicate = true;
                    break;
                }
            }
        }

        // 4. 执行移除与特效
        if (isDuplicate) {
            GuZhenRen.logger.info("仙蛊重复: " + addedCard.name);

            AbstractDungeon.topLevelEffectsQueue.add(new TextAboveCreatureEffect(
                    Settings.WIDTH / 2.0F,
                    Settings.HEIGHT / 2.0F,
                    TEXT[0],
                    Color.RED.cpy()
            ));

            addedCard.current_x = AbstractDungeon.player.hb.cX;
            addedCard.current_y = AbstractDungeon.player.hb.cY;
            addedCard.target_x = addedCard.current_x;
            addedCard.target_y = addedCard.current_y;

            CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);
            AbstractDungeon.topLevelEffectsQueue.add(new ExhaustCardEffect(addedCard));
            group.removeCard(addedCard);

            // =====================================================================
            // 给予残蛊补偿
            // =====================================================================
            if (group == AbstractDungeon.player.masterDeck) {
                if (!AbstractDungeon.player.hasRelic(XianGuCanHai.ID)) {
                    XianGuCanHai relic = new XianGuCanHai();
                    relic.instantObtain();
                } else {
                    ((XianGuCanHai) AbstractDungeon.player.getRelic(XianGuCanHai.ID)).addCharge();
                }
            }
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