package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.WanWo;
import GuZhenRen.cards.WoLi;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_WanWo extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_WanWo");
    // 使用力道的通用配方背景贴图
    private static final String IMG = "Recipe_LiDao.png";
    private static final String OUTLINE = "Recipe_LiDao.png";

    public Recipe_WanWo() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (我力+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(WoLi.ID); // 核心蛊虫：我力
        return list;
    }

    // 要求必须升级
    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(WoLi.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意力道蛊虫)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 条件A: 必须包含【力道】标签
            if (!c.hasTag(GuZhenRenTags.LI_DAO)) {
                return false;
            }


            // 条件B: 必须是蛊虫牌（1-9转）
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
        return new WanWo();
    }
}