package GuZhenRen.util;

import GuZhenRen.cards.FangWeiGu;
import GuZhenRen.relics.AbstractRecipeRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public class ShaZhaoHelper {

    public static ArrayList<AbstractRecipeRelic> getCraftableRecipes() {
        ArrayList<AbstractRecipeRelic> craftableList = new ArrayList<>();
        ArrayList<AbstractRelic> playerRelics = AbstractDungeon.player.relics;

        for (AbstractRelic r : playerRelics) {
            if (r instanceof AbstractRecipeRelic) {
                AbstractRecipeRelic recipe = (AbstractRecipeRelic) r;
                if (canCraft(recipe)) {
                    craftableList.add(recipe);
                }
            }
        }
        return craftableList;
    }

    public static boolean canCraft(AbstractRecipeRelic recipe) {
        // 创建临时牌组模拟
        ArrayList<AbstractCard> tempDeck = new ArrayList<>(AbstractDungeon.player.masterDeck.group);

        // 使用总数
        int count = recipe.getIngredientCount();
        ArrayList<String> fixedIDs = recipe.getRequiredCardIDs();

        // 按顺序检查每一个材料
        for (int i = 0; i < count; i++) {
            boolean found = false;

            // 情况 A: 固定 ID
            if (i < fixedIDs.size()) {
                String reqID = fixedIDs.get(i);
                boolean needUpgrade = recipe.requiresUpgrade(reqID);

                for (int j = 0; j < tempDeck.size(); j++) {
                    AbstractCard c = tempDeck.get(j);
                    if (c.cardID.equals(reqID)) {
                        if (needUpgrade && !c.upgraded) continue;
                        tempDeck.remove(j); // 消耗掉，防止重复使用
                        found = true;
                        break;
                    }
                }
            }
            // 情况 B: 泛型材料
            else {
                for (int j = 0; j < tempDeck.size(); j++) {
                    AbstractCard c = tempDeck.get(j);
                    if (recipe.isGenericIngredient(i, c)) {
                        tempDeck.remove(j); // 消耗掉
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                for (int j = 0; j < tempDeck.size(); j++) {
                    AbstractCard c = tempDeck.get(j);
                    if (c.cardID.equals(FangWeiGu.ID) && c.upgraded) {
                        tempDeck.remove(j);
                        found = true;
                        break;
                    }
                }
            }

            if (!found) return false;
        }

        return true;
    }
}