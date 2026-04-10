package GuZhenRen.patches;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "getRewardCards"
)
public class CardRewardPatch {

    public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
        if (AbstractDungeon.player == null) return __result;

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

        // 3. 遍历掉落的卡牌，判定是否需要替换（现在只判定仙蛊唯一性）
        for (int i = 0; i < __result.size(); i++) {
            AbstractCard c = __result.get(i);

            if (c instanceof AbstractGuZhenRenCard) {
                AbstractGuZhenRenCard guCard = (AbstractGuZhenRenCard) c;

                // 判断是否是玩家已经拥有的仙蛊
                if (guCard.isXianGu() && playerXianGuIDs.contains(guCard.cardID)) {
                    GuZhenRen.logger.info("过滤掉落 [" + guCard.name + "] 原因: 已有同名仙蛊");

                    // 传入索引 i 作为固定种子的参数
                    AbstractCard replacement = getReplacementCard(
                            c.rarity,
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

    private static AbstractCard getReplacementCard(AbstractCard.CardRarity rarity, boolean needUpgrade, Set<String> currentRewardIDs, Set<String> playerXianGuIDs, int slotIndex) {
        AbstractCard.CardRarity[] searchOrder;
        if (rarity == AbstractCard.CardRarity.RARE) {
            searchOrder = new AbstractCard.CardRarity[]{AbstractCard.CardRarity.RARE, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardRarity.COMMON};
        } else if (rarity == AbstractCard.CardRarity.UNCOMMON) {
            searchOrder = new AbstractCard.CardRarity[]{AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardRarity.COMMON};
        } else {
            searchOrder = new AbstractCard.CardRarity[]{AbstractCard.CardRarity.COMMON};
        }

        AbstractCard card = searchForCard(searchOrder, needUpgrade, currentRewardIDs, playerXianGuIDs, slotIndex);

        if (card == null) {
            card = searchForCard(searchOrder, needUpgrade, null, playerXianGuIDs, slotIndex);
        }
        return card;
    }

    private static AbstractCard searchForCard(AbstractCard.CardRarity[] searchOrder, boolean needUpgrade, Set<String> excludeRewardIDs, Set<String> playerXianGuIDs, int slotIndex) {
        for (AbstractCard.CardRarity currentSearchRarity : searchOrder) {
            ArrayList<AbstractCard> candidates = getValidCandidates(currentSearchRarity, needUpgrade, excludeRewardIDs, playerXianGuIDs);

            if (!candidates.isEmpty()) {
                long independentSeed = Settings.seed
                        ^ (AbstractDungeon.floorNum * 31415926535L)
                        ^ (slotIndex * 2718281828L);

                com.megacrit.cardcrawl.random.Random deterministicRng = new com.megacrit.cardcrawl.random.Random(independentSeed);

                candidates.sort(Comparator.comparing(card -> card.cardID));

                AbstractCard chosen = candidates.get(deterministicRng.random(candidates.size() - 1));

                AbstractGuZhenRenCard finalCard = (AbstractGuZhenRenCard) chosen.makeCopy();
                if (needUpgrade) finalCard.upgrade();
                return finalCard;
            }
        }
        return null;
    }

    private static ArrayList<AbstractCard> getValidCandidates(AbstractCard.CardRarity rarity, boolean needUpgrade, Set<String> excludeRewardIDs, Set<String> playerXianGuIDs) {
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

                if (simulatedXianGu && playerXianGuIDs != null && playerXianGuIDs.contains(guCard.cardID)) continue;

                validCandidates.add(c);
            } else {
                validCandidates.add(c);
            }
        }
        return validCandidates;
    }
}