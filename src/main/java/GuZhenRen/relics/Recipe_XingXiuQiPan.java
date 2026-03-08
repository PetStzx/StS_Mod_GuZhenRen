package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.XingXiuQiPan; // 奖励牌
import GuZhenRen.cards.ZhiHuiGu;     // 固定材料
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_XingXiuQiPan extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_XingXiuQiPan");
    private static final String IMG = "Recipe_ZhiDao.png";
    private static final String OUTLINE = "Recipe_ZhiDao.png";

    public Recipe_XingXiuQiPan() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    //  步骤 1：固定材料
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(ZhiHuiGu.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        return false;
    }

    //  步骤 2：泛型材料

    // 1. 总步骤数：2
    @Override
    public int getIngredientCount() {
        return 2;
    }

    // 2. 泛型判定逻辑 (index=1)
    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 条件1: 智道
            if (!c.hasTag(GuZhenRenTags.ZHI_DAO)) {
                return false;
            }


            // 条件2: 六转及以上
            if (c instanceof AbstractGuZhenRenCard) {
                return ((AbstractGuZhenRenCard) c).rank >= 6;
            }

            return false;
        }
        return false;
    }

    // 3. UI提示文本
    @Override
    public String getIngredientDescription(int index) {
        if (index == 1) {
            return this.DESCRIPTIONS[1];
        }
        return super.getIngredientDescription(index);
    }

    // =========================================================================
    //  奖励配置
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        // 返回 星宿棋盘
        return new XingXiuQiPan();
    }
}