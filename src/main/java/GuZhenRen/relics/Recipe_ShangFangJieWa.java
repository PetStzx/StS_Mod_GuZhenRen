package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.HengChongZhiZhuangGu;
import GuZhenRen.cards.ShangFangJieWa;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_ShangFangJieWa extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_ShangFangJieWa");

    // 使用力道的通用配方背景贴图
    private static final String IMG = "Recipe_LiDao.png";
    private static final String OUTLINE = "Recipe_LiDao.png";

    public Recipe_ShangFangJieWa() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (横冲直撞蛊+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(HengChongZhiZhuangGu.ID); // 核心材料：横冲直撞蛊
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(HengChongZhiZhuangGu.ID)) {
            return true; // 要求必须升级
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意力道仙蛊，6-9转)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 条件1: 包含【力道】标签
            if (!c.hasTag(GuZhenRenTags.LI_DAO)) {
                return false;
            }


            // 条件2: 必须是仙蛊
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

    // =========================================================================
    //  奖励发放
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        // 返回合成好的杀招牌：上房揭瓦
        return new ShangFangJieWa();
    }
}