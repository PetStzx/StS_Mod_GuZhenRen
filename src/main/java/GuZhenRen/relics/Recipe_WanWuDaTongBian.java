package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.BianTong;
import GuZhenRen.cards.WanWuDaTongBian;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_WanWuDaTongBian extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_WanWuDaTongBian");
    private static final String IMG = "Recipe_BianHuaDao.png";
    private static final String OUTLINE = "Recipe_BianHuaDao.png";

    public Recipe_WanWuDaTongBian() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (变通+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(BianTong.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(BianTong.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意变化道 或 力道 蛊虫)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 条件 A: 必须包含【变化道】或【力道】标签之一
            boolean hasValidTag = c.hasTag(GuZhenRenTags.BIAN_HUA_DAO) || c.hasTag(GuZhenRenTags.LI_DAO);
            if (!hasValidTag) {
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


    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new WanWuDaTongBian();
    }
}