package GuZhenRen.patches;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.relics.AbstractKongQiao;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "getRewardCards"
)
public class CardRewardPatch {

    public static final int MAX_RANK_DIFF_UP = 1;
    public static final int MAX_RANK_DIFF_DOWN = 99;

    public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
        // 安全检查：如果不在游戏中（如主菜单调试），直接返回
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
                if (c instanceof AbstractGuZhenRenCard) {
                    // 这里判断：只要是仙蛊，就记录ID
                    if (((AbstractGuZhenRenCard) c).isXianGu()) {
                        playerXianGuIDs.add(c.cardID);
                    }
                }
            }
        }

        for (int i = 0; i < __result.size(); i++) {
            AbstractCard c = __result.get(i);

            if (c instanceof AbstractGuZhenRenCard) {
                AbstractGuZhenRenCard guCard = (AbstractGuZhenRenCard) c;
                boolean needReplace = false;
                String replaceReason = "";

                // --- 检测 1: 转数是否合规 ---
                if (!isRankValid(guCard, playerRank)) {
                    needReplace = true;
                    replaceReason = "转数不符(玩家" + playerRank + "转 vs 卡牌" + guCard.rank + "转)";
                }
                // --- 检测 2: 仙蛊唯一性冲突 ---
                else if (guCard.isXianGu() && playerXianGuIDs.contains(guCard.cardID)) {
                    needReplace = true;
                    replaceReason = "仙蛊冲突(已有同名仙蛊)";
                }

                // --- 执行替换 ---
                if (needReplace) {
                    GuZhenRen.logger.info("过滤掉落 [" + guCard.name + "] 原因: " + replaceReason);

                    // 【优化】 将 playerXianGuIDs 传递进去，避免重复遍历 masterDeck
                    AbstractCard replacement = getReplacementCard(
                            c.rarity,
                            playerRank,
                            c.upgraded,
                            currentRewardIDs,
                            playerXianGuIDs
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
        int cardRank = card.rank;
        if (cardRank > playerRank + MAX_RANK_DIFF_UP) return false;
        if (playerRank - cardRank > MAX_RANK_DIFF_DOWN) return false;
        return true;
    }

    private static AbstractCard getReplacementCard(AbstractCard.CardRarity rarity, int playerRank, boolean needUpgrade, Set<String> currentRewardIDs, Set<String> playerXianGuIDs) {
        AbstractCard.CardRarity[] searchOrder;
        if (rarity == AbstractCard.CardRarity.RARE) {
            searchOrder = new AbstractCard.CardRarity[]{AbstractCard.CardRarity.RARE, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardRarity.COMMON};
        } else if (rarity == AbstractCard.CardRarity.UNCOMMON) {
            searchOrder = new AbstractCard.CardRarity[]{AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardRarity.COMMON};
        } else {
            searchOrder = new AbstractCard.CardRarity[]{AbstractCard.CardRarity.COMMON};
        }

        // 第一次尝试：严格模式（排除当前掉落重复 + 排除玩家仙蛊重复）
        AbstractCard card = searchForCard(searchOrder, playerRank, needUpgrade, currentRewardIDs, playerXianGuIDs);

        if (card == null) {
            GuZhenRen.logger.info("   -> 警告：卡池不足，允许重复以保证掉落。");
            // 第二次尝试：放宽限制（只排除玩家仙蛊重复，允许掉落列表内部重复）
            // 注意：通常我们还是不希望给玩家重复的仙蛊，所以 playerXianGuIDs 依然传进去
            card = searchForCard(searchOrder, playerRank, needUpgrade, null, playerXianGuIDs);
        }
        return card;
    }

    private static AbstractCard searchForCard(AbstractCard.CardRarity[] searchOrder, int playerRank, boolean needUpgrade, Set<String> excludeRewardIDs, Set<String> playerXianGuIDs) {
        for (AbstractCard.CardRarity currentSearchRarity : searchOrder) {
            ArrayList<AbstractCard> candidates = getValidCandidates(currentSearchRarity, playerRank, needUpgrade, excludeRewardIDs, playerXianGuIDs);

            if (!candidates.isEmpty()) {
                // 使用 cardRng 保证种子一致性
                // 注意：random(int) 返回 0 到 n-1，正好对应 ArrayList 索引
                AbstractCard chosen = candidates.get(AbstractDungeon.cardRng.random(candidates.size() - 1));

                AbstractGuZhenRenCard finalCard = (AbstractGuZhenRenCard) chosen.makeCopy();
                if (needUpgrade) finalCard.upgrade();
                return finalCard;
            }
        }
        return null;
    }

    private static ArrayList<AbstractCard> getValidCandidates(AbstractCard.CardRarity rarity, int playerRank, boolean needUpgrade, Set<String> excludeRewardIDs, Set<String> playerXianGuIDs) {
        ArrayList<AbstractCard> sourcePool;
        // 获取对应的卡池
        switch (rarity) {
            case COMMON:
                sourcePool = AbstractDungeon.commonCardPool.group;
                break;
            case UNCOMMON:
                sourcePool = AbstractDungeon.uncommonCardPool.group;
                break;
            case RARE:
                sourcePool = AbstractDungeon.rareCardPool.group;
                break;
            default:
                sourcePool = AbstractDungeon.commonCardPool.group;
                break;
        }

        ArrayList<AbstractCard> validCandidates = new ArrayList<>();

        // 遍历卡池寻找合格替补
        for (AbstractCard c : sourcePool) {
            // 1. 排除当前掉落列表中已存在的卡 (防止三张选项里有两张一样的)
            if (excludeRewardIDs != null && excludeRewardIDs.contains(c.cardID)) continue;

            if (c instanceof AbstractGuZhenRenCard) {
                // 需要创建一个副本并升级，因为 rank 和 isXianGu 可能会随升级变化
                AbstractGuZhenRenCard copy = (AbstractGuZhenRenCard) c.makeCopy();
                if (needUpgrade) copy.upgrade();

                // 2. 转数检查
                if (!isRankValid(copy, playerRank)) continue;

                // 3. 仙蛊唯一性检查
                // 如果这张候选牌是仙蛊，且玩家手里已经有了，则不能选它
                if (copy.isXianGu()) {
                    if (playerXianGuIDs != null && playerXianGuIDs.contains(copy.cardID)) {
                        continue;
                    }
                }

                validCandidates.add(c);
            }
        }
        return validCandidates;
    }
}