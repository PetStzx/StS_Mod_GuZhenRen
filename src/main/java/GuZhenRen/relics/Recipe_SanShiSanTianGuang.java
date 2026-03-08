package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.GuangGu; // 固定材料：光蛊
import GuZhenRen.cards.SanShiSanTianGuang; // 奖励牌：三十三天光
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_SanShiSanTianGuang extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_SanShiSanTianGuang");

    private static final String IMG = "Recipe_GuangDao.png";
    private static final String OUTLINE = "Recipe_GuangDao.png";

    public Recipe_SanShiSanTianGuang() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (光蛊)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(GuangGu.ID); // 第一个固定位：光蛊
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        // 光蛊不需要升级 (任何转数都可以)
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意光道蛊虫)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) { // 第二个材料槽
            // 判定 1: 必须有【光道】标签
            if (!c.hasTag(GuZhenRenTags.GUANG_DAO)) {
                return false;
            }

            // 判定 2: 必须是蛊虫
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
        return new SanShiSanTianGuang();
    }
}