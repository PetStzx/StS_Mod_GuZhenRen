package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.BaiGuZhanChe;
import GuZhenRen.cards.ZhanGuCheLun;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_BaiGuZhanChe extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_BaiGuZhanChe");
    private static final String IMG = "Recipe_GuDao.png";
    private static final String OUTLINE = "Recipe_GuDao.png";

    public Recipe_BaiGuZhanChe() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  固定材料 (战骨车轮+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(ZhanGuCheLun.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(ZhanGuCheLun.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  泛型材料 (任意骨道蛊虫)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            if (!c.hasTag(GuZhenRenTags.GU_DAO)) {
                return false;
            }

            if (c instanceof AbstractGuZhenRenCard) {
                int rank = ((AbstractGuZhenRenCard) c).rank;
                return rank >= 1 && rank <= 9;
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
        return new BaiGuZhanChe();
    }
}