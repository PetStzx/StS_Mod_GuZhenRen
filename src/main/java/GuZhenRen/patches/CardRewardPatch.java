package GuZhenRen.patches;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.relics.AbstractKongQiao;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "getRewardCards"
)
public class CardRewardPatch {

    public static final int MAX_RANK_DIFF_UP = 1;
    public static final int MAX_RANK_DIFF_DOWN = 99;

    public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
        if (AbstractDungeon.player == null) return __result;

        int playerRank = AbstractKongQiao.getCurrentRank();

        // 1. 收集当前掉落列表已有的ID，防止单次掉落内部重复
        Set<String> currentRewardIDs = new HashSet<>();
        for (AbstractCard c : __result) {
            currentRewardIDs.add(c.cardID);
        }

        // 2. 收集玩家牌组中所有“仙蛊”的 ID
        Set<String> playerXianGuIDs = new HashSet<>();
        if (AbstractDungeon.player.masterDeck != null) {
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c instanceof AbstractGuZhenRenCard && ((AbstractGuZhenRenCard) c).isXianGu()) {
                    playerXianGuIDs.add(c.cardID);
                }
            }
        }

        // 3. 遍历掉落的卡牌，判定是否需要替换
        for (int i = 0; i < __result.size(); i++) {
            AbstractCard c = __result.get(i);

            // 只对蛊真人体系的卡牌进行转数和仙蛊判定
            if (c instanceof AbstractGuZhenRenCard) {
                AbstractGuZhenRenCard guCard = (AbstractGuZhenRenCard) c;

                boolean needReplace = false;
                String replaceReason = "";

                if (!isRankValid(guCard, playerRank)) {
                    needReplace = true;
                    replaceReason = "转数不符(玩家" + playerRank + "转 vs 卡牌" + guCard.rank + "转)";
                } else if (guCard.isXianGu() && playerXianGuIDs.contains(guCard.cardID)) {
                    needReplace = true;
                    replaceReason = "仙蛊冲突(已有同名仙蛊)";
                }

                if (needReplace) {
                    GuZhenRen.logger.info("过滤掉落 [" + guCard.name + "] 原因: " + replaceReason);

                    // 传入索引 i 作为固定种子的参数，确保每次 SL 替换结果绝对一致
                    AbstractCard replacement = getReplacementCard(
                            c.rarity,
                            playerRank,
                            c.upgraded,
                            currentRewardIDs,
                            playerXianGuIDs,
                            i
                    );

                    if (replacement != null) {
                        __result.set(i, replacement);
                        currentRewardIDs.add(replacement.cardID);
                        GuZhenRen.logger.info("   -> 替换为：" + replacement.name);
                    } else {
                        GuZhenRen.logger.info("   -> 替换失败：无可用卡牌");
                    }
                }
            }
        }

        return __result;
    }

    private static boolean isRankValid(AbstractGuZhenRenCard card, int playerRank) {
        if (card.rank > playerRank + MAX_RANK_DIFF_UP) return false;
        if (playerRank - card.rank > MAX_RANK_DIFF_DOWN) return false;
        return true;
    }

    private static AbstractCard getReplacementCard(AbstractCard.CardRarity rarity, int playerRank, boolean needUpgrade, Set<String> currentRewardIDs, Set<String> playerXianGuIDs, int slotIndex) {
        AbstractCard.CardRarity[] searchOrder;
        if (rarity == AbstractCard.CardRarity.RARE) {
            searchOrder = new AbstractCard.CardRarity[]{AbstractCard.CardRarity.RARE, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardRarity.COMMON};
        } else if (rarity == AbstractCard.CardRarity.UNCOMMON) {
            searchOrder = new AbstractCard.CardRarity[]{AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardRarity.COMMON};
        } else {
            searchOrder = new AbstractCard.CardRarity[]{AbstractCard.CardRarity.COMMON};
        }

        AbstractCard card = searchForCard(searchOrder, playerRank, needUpgrade, currentRewardIDs, playerXianGuIDs, slotIndex);

        if (card == null) {
            card = searchForCard(searchOrder, playerRank, needUpgrade, null, playerXianGuIDs, slotIndex);
        }
        return card;
    }

    private static AbstractCard searchForCard(AbstractCard.CardRarity[] searchOrder, int playerRank, boolean needUpgrade, Set<String> excludeRewardIDs, Set<String> playerXianGuIDs, int slotIndex) {
        for (AbstractCard.CardRarity currentSearchRarity : searchOrder) {
            ArrayList<AbstractCard> candidates = getValidCandidates(currentSearchRarity, playerRank, needUpgrade, excludeRewardIDs, playerXianGuIDs);

            if (!candidates.isEmpty()) {
                // 脱离游戏引擎，用纯数学构建一个只和“全局种子+楼层+槽位”挂钩的固定随机生成器
                long independentSeed = Settings.seed + (AbstractDungeon.floorNum * 10L) + slotIndex;
                Random deterministicRng = new Random(independentSeed);

                // 强制对候选卡牌按 ID 排序，保证在任何内存状态下，同一个种子抽出的牌绝对一样
                candidates.sort(Comparator.comparing(card -> card.cardID));

                AbstractCard chosen = candidates.get(deterministicRng.nextInt(candidates.size()));

                AbstractGuZhenRenCard finalCard = (AbstractGuZhenRenCard) chosen.makeCopy();
                if (needUpgrade) finalCard.upgrade();
                return finalCard;
            }
        }
        return null;
    }

    private static ArrayList<AbstractCard> getValidCandidates(AbstractCard.CardRarity rarity, int playerRank, boolean needUpgrade, Set<String> excludeRewardIDs, Set<String> playerXianGuIDs) {
        ArrayList<AbstractCard> sourcePool;
        switch (rarity) {
            case COMMON: sourcePool = AbstractDungeon.commonCardPool.group; break;
            case UNCOMMON: sourcePool = AbstractDungeon.uncommonCardPool.group; break;
            case RARE: sourcePool = AbstractDungeon.rareCardPool.group; break;
            default: sourcePool = AbstractDungeon.commonCardPool.group; break;
        }

        ArrayList<AbstractCard> validCandidates = new ArrayList<>();

        for (AbstractCard c : sourcePool) {
            if (excludeRewardIDs != null && excludeRewardIDs.contains(c.cardID)) continue;

            if (c instanceof AbstractGuZhenRenCard) {
                AbstractGuZhenRenCard guCard = (AbstractGuZhenRenCard) c;

                int simulatedRank = Math.min(9, guCard.baseRank + (needUpgrade ? 1 : 0));
                boolean simulatedXianGu = guCard.tags.contains(GuZhenRenTags.XIAN_GU) || simulatedRank >= 6;

                if (simulatedRank > playerRank + MAX_RANK_DIFF_UP) continue;
                if (playerRank - simulatedRank > MAX_RANK_DIFF_DOWN) continue;
                if (simulatedXianGu && playerXianGuIDs != null && playerXianGuIDs.contains(guCard.cardID)) continue;

                validCandidates.add(c);
            } else {
                // 非蛊真人牌（如棱镜带来的其他牌），直接作为合法的候选项加入
                validCandidates.add(c);
            }
        }
        return validCandidates;
    }
}