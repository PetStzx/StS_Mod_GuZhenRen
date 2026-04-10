package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.LengXue;
import GuZhenRen.cards.XueJianLeng;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_XueJianLeng extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_XueJianLeng");
    private static final String IMG = "Recipe_XueDao.png";
    private static final String OUTLINE = "Recipe_XueDao.png";

    public Recipe_XueJianLeng() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (冷血+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(LengXue.ID);
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(LengXue.ID)) {
            return true;
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意血道蛊虫，1-9转)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 判定 1: 必须有【血道】标签
            if (!c.hasTag(GuZhenRenTags.XUE_DAO)) {
                return false;
            }


            // 判定 3: 必须是蛊虫，且拥有 1-9 的转数
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

    //  奖励发放
    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new XueJianLeng(); // 返回杀招：血渐冷
    }
}