package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.TianYuanBaoLian;
import GuZhenRen.cards.YinGuoShenShu;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_YinGuoShenShu extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_YinGuoShenShu");
    private static final String IMG = "Recipe_MuDao.png";
    private static final String OUTLINE = "Recipe_MuDao.png";

    public Recipe_YinGuoShenShu() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  固定材料 (天元宝莲+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(TianYuanBaoLian.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(TianYuanBaoLian.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  泛型材料 (任意律道 或 运道 仙蛊)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            boolean hasValidTag = c.hasTag(GuZhenRenTags.LU_DAO) || c.hasTag(GuZhenRenTags.YUN_DAO);
            if (!hasValidTag) {
                return false;
            }

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
        return new YinGuoShenShu();
    }

    @Override
    protected String[] getTipKeywords(){
        return new String[]{"果"};
    }
}