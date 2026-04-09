package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.BaMianWeiFengGu;
import GuZhenRen.cards.SongYouFeng;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_SongYouFeng extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_SongYouFeng");
    private static final String IMG = "Recipe_FengDao.png";
    private static final String OUTLINE = "Recipe_FengDao.png";

    public Recipe_SongYouFeng() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (八面威风蛊)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(BaMianWeiFengGu.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(BaMianWeiFengGu.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意风道或律道蛊虫)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 条件 A: 必须包含【风道】或【律道】标签之一
            boolean hasValidTag = c.hasTag(GuZhenRenTags.FENG_DAO) || c.hasTag(GuZhenRenTags.LU_DAO);
            if (!hasValidTag) {
                return false;
            }

            // 条件 B: 必须是蛊虫牌（1-9转皆可）
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
        return new SongYouFeng();
    }

    @Override
    protected String[] getTipKeywords(){
        return new String[]{"好友"};
    }
}