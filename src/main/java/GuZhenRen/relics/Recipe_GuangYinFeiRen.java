package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.RenGu;
import GuZhenRen.cards.GuangYinFeiRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_GuangYinFeiRen extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_GuangYinFeiRen");
    private static final String IMG = "Recipe_ZhouDao.png";
    private static final String OUTLINE = "Recipe_ZhouDao.png";

    public Recipe_GuangYinFeiRen() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (刃蛊)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(RenGu.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意宙道仙蛊)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 条件 A: 必须包含【宙道】标签
            if (!c.hasTag(GuZhenRenTags.ZHOU_DAO)) {
                return false;
            }

            // 条件 B: 必须是仙蛊（6-9转）
            if (c instanceof AbstractGuZhenRenCard) {
                int rank = ((AbstractGuZhenRenCard) c).rank;
                return rank >= 6 && rank <= 9;
            }

            return false;
        }
        return false;
    }

    @Override
    public String getIngredientDescription(int index) {
        if (index == 1) {
            return this.DESCRIPTIONS[1];
        }
        return super.getIngredientDescription(index);
    }


    //  奖励发放
    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new GuangYinFeiRen();
    }
}