package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.WanXingFeiYing;
import GuZhenRen.cards.XingNianGu;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_WanXingFeiYing extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_WanXingFeiYing");
    private static final String IMG = "Recipe_ZhiDao.png";
    private static final String OUTLINE = "Recipe_ZhiDao.png";

    public Recipe_WanXingFeiYing() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        // "需要： 星念蛊+ 、任意 6 转及以上的 智道仙蛊 。"
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (星念蛊+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(XingNianGu.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(XingNianGu.ID)) return true;
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (6转以上智道蛊虫)
    // =========================================================================

    // 1. 总步骤数：2
    @Override
    public int getIngredientCount() {
        return 2;
    }

    // 2. 泛型判定逻辑 (index=1)
    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 条件A: 智道
            if (!c.hasTag(GuZhenRenTags.ZHI_DAO)) {
                return false;
            }

            // 条件B: 排除星念蛊
            if (c.cardID.equals(XingNianGu.ID)) {
                return false;
            }

            // 条件C: 六转及以上
            // 只要继承自 AbstractGuZhenRenCard 都有 rank 属性 (包括本命蛊、普通牌)
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
            return "智道仙蛊";
        }
        return super.getIngredientDescription(index);
    }

    // =========================================================================
    //  基础配置
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new WanXingFeiYing();
    }
}