package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.JianMianCengXiangShi;
import GuZhenRen.cards.TaiDuGu;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_JianMianCengXiangShi extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_JianMianCengXiangShi");
    private static final String IMG = "Recipe_BianHuaDao.png";
    private static final String OUTLINE = "Recipe_BianHuaDao.png";

    public Recipe_JianMianCengXiangShi() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }


    @Override
    public boolean canBeBorrowedByWeiLaiShen() {
        return false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (态度蛊+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(TaiDuGu.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(TaiDuGu.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意变化道 或 偷道 蛊虫)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            boolean hasValidTag = c.hasTag(GuZhenRenTags.BIAN_HUA_DAO) || c.hasTag(GuZhenRenTags.TOU_DAO);
            if (!hasValidTag) {
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
        return new JianMianCengXiangShi();
    }

    @Override
    protected String[] getTipKeywords(){
        return new String[]{"持续性", "好友"};
    }
}