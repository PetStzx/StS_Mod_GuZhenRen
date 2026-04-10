package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.HuiJian;
import GuZhenRen.cards.WuZhiQuanXinJian;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_WuZhiQuanXinJian extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_WuZhiQuanXinJian");
    private static final String IMG = "Recipe_JianDao.png";
    private static final String OUTLINE = "Recipe_JianDao.png";

    public Recipe_WuZhiQuanXinJian() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (慧剑+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(HuiJian.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(HuiJian.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意剑道蛊虫，1-9转)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 判定 1: 必须有【剑道】标签
            if (!c.hasTag(GuZhenRenTags.JIAN_DAO)) {
                return false;
            }


            // 判定 2: 必须是蛊虫，且拥有 1-9 的转数
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
        return new WuZhiQuanXinJian();
    }
}