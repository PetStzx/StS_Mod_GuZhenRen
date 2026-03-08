package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.LiaoYuanHuo;
import GuZhenRen.cards.ZhuiMingHuo;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_ZhuiMingHuo extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_ZhuiMingHuo");
    private static final String IMG = "Recipe_YanDao.png";
    private static final String OUTLINE = "Recipe_YanDao.png";

    public Recipe_ZhuiMingHuo() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (燎原火+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(LiaoYuanHuo.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(LiaoYuanHuo.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意炎道蛊虫，1-9转)
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

            // 判定 2: 必须是【蛊虫】(继承自 AbstractGuZhenRenCard)
            if (!(c instanceof AbstractGuZhenRenCard)) {
                return false;
            }

            // 判定 3: 必须拥有 1-9 的转数
            int rank = ((AbstractGuZhenRenCard) c).rank;
            if (rank < 1 || rank > 9) {
                return false;
            }


            return true;
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
    //  奖励
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new ZhuiMingHuo();
    }
}