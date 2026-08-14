package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.LuanFangHunXiangWu;
import GuZhenRen.cards.SheXinGu;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_LuanFangHunXiangWu extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_LuanFangHunXiangWu");
    private static final String IMG = "Recipe_ZhiDao.png";
    private static final String OUTLINE = "Recipe_ZhiDao.png";

    public Recipe_LuanFangHunXiangWu() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  固定材料 (摄心蛊+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(SheXinGu.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(SheXinGu.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  泛型材料 (任意智道仙蛊)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            if (!c.hasTag(GuZhenRenTags.ZHI_DAO)) {
                return false;
            }

            if (c instanceof AbstractGuZhenRenCard) {
                return ((AbstractGuZhenRenCard) c).rank >= 6;
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

    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new LuanFangHunXiangWu();
    }
}