package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.Xian;
import GuZhenRen.cards.WuJinXuanGuangQi;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_WuJinXuanGuangQi extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_WuJinXuanGuangQi");
    private static final String IMG = "Recipe_LuDao.png";
    private static final String OUTLINE = "Recipe_LuDao.png";

    public Recipe_WuJinXuanGuangQi() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (限+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Xian.ID); // 第一个固定位：限
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(Xian.ID)) {
            return true; // 必须升级过
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意律道蛊虫)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 条件 A: 必须包含【律道】标签
            if (!c.hasTag(GuZhenRenTags.LU_DAO)) {
                return false;
            }

            // 条件 B: 必须是蛊虫牌（1-9转）
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

    // =========================================================================
    //  奖励发放
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        // 返回合成好的杀招牌：五禁玄光气
        return new WuJinXuanGuangQi();
    }
}