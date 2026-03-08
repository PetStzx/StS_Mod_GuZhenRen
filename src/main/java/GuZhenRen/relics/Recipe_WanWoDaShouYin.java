package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.BaShan;
import GuZhenRen.cards.WanWoDaShouYin;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_WanWoDaShouYin extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_WanWoDaShouYin");
    private static final String IMG = "Recipe_LiDao.png";
    private static final String OUTLINE = "Recipe_LiDao.png";

    public Recipe_WanWoDaShouYin() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (拔山+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(BaShan.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(BaShan.ID)) {
            return true; // 必须升级
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意力道仙蛊)
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


            // 条件B: 必须是仙蛊 (6转及以上)
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
    //  奖励发放
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new WanWoDaShouYin();
    }
}