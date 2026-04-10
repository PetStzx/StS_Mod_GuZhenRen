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
    //  步骤 2：泛型材料 (任意智道仙蛊)
    // =========================================================================

    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 条件A: 智道
            if (!c.hasTag(GuZhenRenTags.ZHI_DAO)) {
                return false;
            }


            // 条件B: 六转及以上
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

    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new WanXingFeiYing();
    }
}