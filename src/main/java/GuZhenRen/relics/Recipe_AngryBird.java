package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.AngryBird;
import GuZhenRen.cards.RongYanZhaLieGu;
import GuZhenRen.patches.GuZhenRenTags; // 别忘了导入标签
import com.megacrit.cardcrawl.cards.AbstractCard;
import java.util.ArrayList;

public class Recipe_AngryBird extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_AngryBird");
    private static final String IMG = "Recipe_YanDao.png";
    private static final String OUTLINE = "Recipe_YanDao.png";

    public Recipe_AngryBird() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (熔岩炸裂蛊+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(RongYanZhaLieGu.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(RongYanZhaLieGu.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意炎道仙蛊)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 判定 1: 必须有【炎道】标签
            if (!c.hasTag(GuZhenRenTags.YAN_DAO)) {
                return false;
            }

            // 判定 2: 排除熔岩炸裂蛊本身
            if (c.cardID.equals(RongYanZhaLieGu.ID)) {
                return false;
            }

            // 判定 3: 必须是蛊虫，且转数 >= 6 (仙蛊级别)，最高为9
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
            return "任意炎道仙蛊";
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
        return new AngryBird();
    }
}