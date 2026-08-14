package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.XueMuTianHuaGu;
import GuZhenRen.cards.XueRanZhengPao;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_XueRanZhengPao extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_XueRanZhengPao");
    private static final String IMG = "Recipe_XueDao.png";
    private static final String OUTLINE = "Recipe_XueDao.png";

    public Recipe_XueRanZhengPao() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  固定材料 (血幕天华蛊+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(XueMuTianHuaGu.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(XueMuTianHuaGu.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  泛型材料 (任意血道蛊虫)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            if (!c.hasTag(GuZhenRenTags.XUE_DAO)) {
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
        return new XueRanZhengPao();
    }
}