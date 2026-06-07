package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.AnTuZhongShanBao;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_AnTuZhongShanBao extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_AnTuZhongShanBao");
    private static final String IMG = "Recipe_TuDao.png";
    private static final String OUTLINE = "Recipe_TuDao.png";

    public Recipe_AnTuZhongShanBao() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  固定材料 (无)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        return new ArrayList<>();
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        return false;
    }

    // =========================================================================
    //  泛型材料 (3只任意土道蛊虫)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 3;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        // 必须有【土道】标签
        if (!c.hasTag(GuZhenRenTags.TU_DAO)) {
            return false;
        }

        // 必须是蛊虫
        if (c instanceof AbstractGuZhenRenCard) {
            int rank = ((AbstractGuZhenRenCard) c).rank;
            return rank >= 1 && rank <= 9;
        }

        return false;
    }

    @Override
    public String getIngredientDescription(int index) {
        return this.DESCRIPTIONS[1];
    }


    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new AnTuZhongShanBao();
    }

    @Override
    protected String[] getTipKeywords(){
        return new String[]{"化石"};
    }
}