package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.YangMangBeiHuoYi;
import GuZhenRen.cards.YanZhouGu;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_YangMangBeiHuoYi extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_YangMangBeiHuoYi");
    // 使用炎道的配方背景
    private static final String IMG = "Recipe_YanDao.png";
    private static final String OUTLINE = "Recipe_YanDao.png";

    public Recipe_YangMangBeiHuoYi() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        // 对应 RelicStrings.json 中的描述
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (炎胄蛊+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(YanZhouGu.ID);
        return list;
    }

    // 要求必须升级
    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(YanZhouGu.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (6转及以上的炎道蛊虫)
    // =========================================================================

    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 条件A: 必须是【炎道】
            if (!c.hasTag(GuZhenRenTags.YAN_DAO)) {
                return false;
            }


            // 条件C: 六转及以上
            if (c instanceof AbstractGuZhenRenCard) {
                return ((AbstractGuZhenRenCard) c).rank >= 6;
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
    //  奖励与注册
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new YangMangBeiHuoYi();
    }
}