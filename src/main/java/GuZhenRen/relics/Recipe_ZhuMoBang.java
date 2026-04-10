package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.XueYuan;
import GuZhenRen.cards.ZhuMoBang;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_ZhuMoBang extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_ZhuMoBang");
    private static final String IMG = "Recipe_XueDao.png";
    private static final String OUTLINE = "Recipe_XueDao.png";

    public Recipe_ZhuMoBang() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (血缘+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(XueYuan.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(XueYuan.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意血道仙蛊，>=6转)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 判定 1: 必须有【血道】标签
            if (!c.hasTag(GuZhenRenTags.XUE_DAO)) {
                return false;
            }


            // 判定 2: 必须是仙蛊，转数 >= 6
            if (c instanceof AbstractGuZhenRenCard) {
                int rank = ((AbstractGuZhenRenCard) c).rank;
                return rank >= 6;
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
        return new ZhuMoBang();
    }
}