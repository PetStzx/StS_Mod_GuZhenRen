package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.ShiZhen;
import GuZhenRen.cards.WeiLaiShen;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_WeiLaiShen extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_WeiLaiShen");
    private static final String IMG = "Recipe_ZhouDao.png";
    private static final String OUTLINE = "Recipe_ZhouDao.png";

    public Recipe_WeiLaiShen() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public boolean canBeBorrowedByWeiLaiShen() {
        return false;
    }

    // =========================================================================
    //  固定材料 (时针+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(ShiZhen.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(ShiZhen.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  泛型材料 (任意宙道仙蛊)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            if (!c.hasTag(GuZhenRenTags.ZHOU_DAO)) {
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
        return new WeiLaiShen();
    }

    @Override
    protected String[] getTipKeywords(){
        return new String[]{"持续性", "杀招遗物"};
    }
}