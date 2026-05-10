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

        // 3. 仙蛊唯一性防重
        for (int i = 0; i < __result.size(); i++) {
            AbstractCard c = __result.get(i);

            if (c instanceof AbstractGuZhenRenCard) {
                AbstractGuZhenRenCard guCard = (AbstractGuZhenRenCard) c;

                if (guCard.isXianGu() && playerXianGuIDs.contains(guCard.cardID)) {
                    GuZhenRen.logger.info("过滤掉落 [" + guCard.name + "] 原因: 已有同名仙蛊");

                    AbstractCard replacement = getReplacementCard(c.rarity, c.upgraded, currentRewardIDs, playerXianGuIDs, i);

                    if (replacement != null) {
                        __result.set(i, replacement);
                        currentRewardIDs.add(replacement.cardID);
                        GuZhenRen.logger.info("   -> 替换为：" + replacement.name);
                    }
                }
            }
        }

        // 4. 灾劫战斗后：获取玩家身上的空窍遗物，读取存活下来的灾劫索引
        AbstractKongQiao kq = AbstractKongQiao.getInstance();

        if (kq != null && kq.completedTribulationIndex != -1) {
            AbstractCard.CardTags targetDaoTag = null;

            // 4.1 寻找牌组中第一张本命蛊，决定流派倾向（变化道与杀道目前无倾向）
            if (AbstractDungeon.player.masterDeck != null) {
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c.hasTag(GuZhenRenTags.BEN_MING_GU)) {
                        String id = c.cardID;
                        if (id.equals(GuZhenRen.makeID("LiLiangGu"))) targetDaoTag = GuZhenRenTags.LI_DAO;
                        else if (id.equals(GuZhenRen.makeID("RenGu"))) targetDaoTag = GuZhenRenTags.JIAN_DAO;
                        else if (id.equals(GuZhenRen.makeID("HuoGu"))) targetDaoTag = GuZhenRenTags.YAN_DAO;
                        else if (id.equals(GuZhenRen.makeID("XinXue"))) targetDaoTag = GuZhenRenTags.XUE_DAO;
                        else if (id.equals(GuZhenRen.makeID("ZhiHuiGu"))) targetDaoTag = GuZhenRenTags.ZHI_DAO;
                        break;
                    }
                }
            }

            // 4.2 如果有指定流派，在当前的奖励列表里挑一张变成该流派
            if (targetDaoTag != null) {
                ArrayList<Integer> replaceableIndices = new ArrayList<>();
                for (int i = 0; i < __result.size(); i++) {
                    if (!__result.get(i).tags.contains(targetDaoTag)) {
                        replaceableIndices.add(i);
                    }
                }

                if (!replaceableIndices.isEmpty()) {
                    long seed = Settings.seed ^ (AbstractDungeon.floorNum * 777L);
                    com.megacrit.cardcrawl.random.Random rng = new com.megacrit.cardcrawl.random.Random(seed);

                    int replaceIndex = replaceableIndices.get(rng.random(replaceableIndices.size() - 1));
                    AbstractCard cardToReplace = __result.get(replaceIndex);

                    // 在同稀有度中寻找目标流派的候选牌
                    ArrayList<AbstractCard> candidates = getValidCandidates(cardToReplace.rarity, cardToReplace.upgraded, currentRewardIDs, playerXianGuIDs);
                    ArrayList<AbstractCard> schoolCandidates = new ArrayList<>();

                    for (AbstractCard c : candidates) {
                        if (c.tags.contains(targetDaoTag)) {
                            schoolCandidates.add(c);
                        }
                    }

                    if (!schoolCandidates.isEmpty()) {
                        schoolCandidates.sort(Comparator.comparing(card -> card.cardID));
                        AbstractCard chosen = schoolCandidates.get(rng.random(schoolCandidates.size() - 1));

                        AbstractCard replacement = chosen.makeCopy();
                        if (cardToReplace.upgraded) replacement.upgrade();

                        __result.set(replaceIndex, replacement);
                        currentRewardIDs.add(replacement.cardID);
                        GuZhenRen.logger.info("灾劫奖励卡牌定向：将位置 " + replaceIndex + " 替换为卡牌 " + replacement.name);
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

                AbstractCard finalCard = chosen.makeCopy();
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