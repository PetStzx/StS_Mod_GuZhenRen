package GuZhenRen.patches;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.relics.XianGuCanHai;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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

    // 本命蛊上限
    public static int maxBenMingGuAllowed = 1;

    /**
     * 辅助方法：检查并移除重复的仙蛊 / 本命蛊
     */
    public static void checkAndRemoveDuplicate(CardGroup group, AbstractCard addedCard) {
        // 1. 基础资格检查
        if (addedCard.purgeOnUse || addedCard.isInAutoplay) return;
        if (AbstractDungeon.player == null) return;
        if (addedCard.hasTag(GuZhenRenTags.XU_YING_COPY)) return;

        boolean isBenMing = addedCard.hasTag(GuZhenRenTags.BEN_MING_GU);
        boolean isXian = addedCard.hasTag(GuZhenRenTags.XIAN_GU);

        if (!isBenMing && !isXian) return;

        // 2. 环境检查
        boolean isRelevantGroup = (group == AbstractDungeon.player.masterDeck) ||
                (group == AbstractDungeon.player.hand) ||
                (group == AbstractDungeon.player.drawPile) ||
                (group == AbstractDungeon.player.discardPile) ||
                (group == AbstractDungeon.player.exhaustPile);

        if (!isRelevantGroup) return;

        // 3. 查重逻辑
        boolean isDuplicate = false;
        boolean isBenMingDestroy = false; // 用于标记是否触发了大师牌组的本命蛊毁灭

        if (group == AbstractDungeon.player.masterDeck) {
            if (isBenMing) {
                int benMingCount = 0;
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c != addedCard && c.hasTag(GuZhenRenTags.BEN_MING_GU)) {
                        benMingCount++;
                    }
                }
                // 若本命蛊数量达到上限
                if (benMingCount >= maxBenMingGuAllowed) {
                    isDuplicate = true;
                    isBenMingDestroy = true;
                }
            } else if (isXian) {
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    boolean isFakeCard = c.hasTag(GuZhenRenTags.XU_YING_COPY) || c.purgeOnUse;
                    if (c != addedCard && c.cardID.equals(addedCard.cardID) && c.hasTag(GuZhenRenTags.XIAN_GU) && !isFakeCard) {
                        isDuplicate = true;
                        break;
                    }
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

            if (isBenMing) {
                // 本命蛊战斗内判定：沿用仙蛊逻辑
                int addedRank = 0;
                if (addedCard instanceof AbstractGuZhenRenCard) {
                    addedRank = ((AbstractGuZhenRenCard) addedCard).rank;
                }

                if (addedRank >= 6) {
                    for (AbstractCard c : allCombatCards) {
                        boolean isFakeCard = c.hasTag(GuZhenRenTags.XU_YING_COPY) || c.purgeOnUse;
                        if (c != addedCard && c.cardID.equals(addedCard.cardID) && c.hasTag(GuZhenRenTags.BEN_MING_GU) && !isFakeCard) {
                            int cRank = 0;
                            if (c instanceof AbstractGuZhenRenCard) {
                                cRank = ((AbstractGuZhenRenCard) c).rank;
                            }
                            if (cRank >= 6) {
                                isDuplicate = true;
                                break;
                            }
                        }
                    }
                }
            } else if (isXian) {
                for (AbstractCard c : allCombatCards) {
                    boolean isFakeCard = c.hasTag(GuZhenRenTags.XU_YING_COPY) || c.purgeOnUse;
                    if (c != addedCard && c.cardID.equals(addedCard.cardID) && c.hasTag(GuZhenRenTags.XIAN_GU) && !isFakeCard) {
                        isDuplicate = true;
                        break;
                    }
                }
            }
        }

        // 4. 执行移除与特效
        if (isDuplicate) {
            GuZhenRen.logger.info((isBenMingDestroy ? "本命蛊毁灭: " : "仙蛊重复: ") + addedCard.name);

            if (!isBenMingDestroy) {
                AbstractDungeon.topLevelEffectsQueue.add(new TextAboveCreatureEffect(
                        Settings.WIDTH / 2.0F,
                        Settings.HEIGHT / 2.0F,
                        TEXT[0],
                        Color.RED.cpy()
                ));
            }

            addedCard.current_x = AbstractDungeon.player.hb.cX;
            addedCard.current_y = AbstractDungeon.player.hb.cY;
            addedCard.target_x = addedCard.current_x;
            addedCard.target_y = addedCard.current_y;

            CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);
            AbstractDungeon.topLevelEffectsQueue.add(new ExhaustCardEffect(addedCard));

            Gdx.app.postRunnable(() -> {
                group.removeCard(addedCard);
                if (group == AbstractDungeon.player.hand) {
                    AbstractDungeon.player.hand.refreshHandLayout();
                }
            });

            // 给予残蛊补偿
            if (group == AbstractDungeon.player.masterDeck) {
                boolean shouldCompensate = false;

                if (isBenMingDestroy) {
                    // 判断被销毁的本命蛊转数是否 >= 6
                    int addedRank = 0;
                    if (addedCard instanceof AbstractGuZhenRenCard) {
                        addedRank = ((AbstractGuZhenRenCard) addedCard).rank;
                    }
                    if (addedRank >= 6) {
                        shouldCompensate = true;
                    }
                } else {
                    // 仙蛊默认补偿
                    shouldCompensate = true;
                }

                // 给予补偿
                if (shouldCompensate) {
                    if (!AbstractDungeon.player.hasRelic(XianGuCanHai.ID)) {
                        XianGuCanHai relic = new XianGuCanHai();
                        relic.instantObtain();
                    } else {
                        ((XianGuCanHai) AbstractDungeon.player.getRelic(XianGuCanHai.ID)).addCharge();
                    }
                }
            }
        }
    }

    // 位置查找，用于应对升级的卡牌
    public static void checkCardAnywhere(AbstractCard c) {
        if (AbstractDungeon.player == null) return;

        // 优先检查是否在全局牌组
        if (AbstractDungeon.player.masterDeck.contains(c)) {
            checkAndRemoveDuplicate(AbstractDungeon.player.masterDeck, c);
            return;
        }
        // 战斗内各牌堆检查
        if (AbstractDungeon.player.hand.contains(c)) {
            checkAndRemoveDuplicate(AbstractDungeon.player.hand, c);
        } else if (AbstractDungeon.player.drawPile.contains(c)) {
            checkAndRemoveDuplicate(AbstractDungeon.player.drawPile, c);
        } else if (AbstractDungeon.player.discardPile.contains(c)) {
            checkAndRemoveDuplicate(AbstractDungeon.player.discardPile, c);
        } else if (AbstractDungeon.player.exhaustPile.contains(c)) {
            checkAndRemoveDuplicate(AbstractDungeon.player.exhaustPile, c);
        } else if (AbstractDungeon.player.limbo.contains(c)) {
            checkAndRemoveDuplicate(AbstractDungeon.player.limbo, c);
        }
    }

    // 拦截全局永久升级
    @SpirePatch(clz = AbstractPlayer.class, method = "bottledCardUpgradeCheck")
    public static class UpgradeGlobalPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance, AbstractCard c) {
            checkCardAnywhere(c);
        }
    }

    // 拦截战斗内的临时升级
    @SpirePatch(clz = AbstractCard.class, method = "upgradeName")
    public static class InCombatUpgradePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            if (CardCrawlGame.isInARun() &&
                    AbstractDungeon.currMapNode != null &&
                    AbstractDungeon.getCurrRoom() != null &&
                    AbstractDungeon.getCurrRoom().phase == com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT) {

                AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                    @Override
                    public void update() {
                        checkCardAnywhere(__instance);
                        this.isDone = true;
                    }
                });
            }
        }
    }

    // --- Postfix 位置移动拦截 ---
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