package GuZhenRen.actions;

import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.FangWeiGu;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.CardStrings;

import java.util.ArrayList;
import java.util.HashSet;

public class FangWeiGuAction extends AbstractGameAction {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(FangWeiGu.ID);

    private boolean upgraded;
    private static ArrayList<AbstractCard> validGuCache = null;

    public FangWeiGuAction(boolean upgraded) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        this.upgraded = upgraded;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

            if (this.upgraded) {
                // 升级后：从全图鉴获取 (使用静态缓存优化)
                if (validGuCache == null) {
                    validGuCache = new ArrayList<>();
                    for (AbstractCard c : CardLibrary.getAllCards()) {
                        if (isValidGu(c)) {
                            validGuCache.add(c);
                        }
                    }
                }

                for (AbstractCard c : validGuCache) {
                    // 使用 makeCopy() 确保生成的是最基础、未升级的牌
                    tmp.addToTop(c.makeCopy());
                }
            } else {
                // 升级前：遍历玩家的大师牌组，并进行去重
                HashSet<String> seenIDs = new HashSet<>();
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    // 如果符合条件，且这个 ID 还没有被添加过
                    if (isValidGu(c) && !seenIDs.contains(c.cardID)) {
                        seenIDs.add(c.cardID);
                        // makeCopy() 同样确保生成的是未升级的牌，剥离牌组中的升级状态
                        tmp.addToTop(c.makeCopy());
                    }
                }
            }

            // 排序
            tmp.sortAlphabetically(true);
            tmp.sortByRarityPlusStatusCardType(false);

            if (tmp.isEmpty()) {
                this.isDone = true;
                return;
            }

            String msg = cardStrings.EXTENDED_DESCRIPTION[0];
            AbstractDungeon.gridSelectScreen.open(tmp, 1, msg, false, false, false, false);
            this.tickDuration();
            return;
        }

        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard selectedCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);

            // 拿到未升级的初始牌
            AbstractCard copy = selectedCard.makeCopy();

            // 贴上虚影标签，绕过仙蛊唯一查重
            copy.tags.add(GuZhenRenTags.XU_YING_COPY);

            this.addToBot(new MakeTempCardInHandAction(copy, true));

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        this.isDone = true;
    }

    // 辅助方法：判定是否为合法的蛊虫牌
    private boolean isValidGu(AbstractCard c) {
        if (c instanceof AbstractGuZhenRenCard) {
            int rank = ((AbstractGuZhenRenCard) c).rank;

            // 1. 必须是 1-9 转的蛊虫
            // 2. 排除仿伪蛊自身
            // 3. 排除本命蛊 (双重保险)
            // 4. 排除所有特殊稀有度的牌
            return rank >= 1 && rank <= 9 &&
                    !c.cardID.equals(FangWeiGu.ID) &&
                    !c.hasTag(GuZhenRenTags.BEN_MING_GU) &&
                    c.rarity != AbstractCard.CardRarity.SPECIAL;
        }
        return false;
    }
}