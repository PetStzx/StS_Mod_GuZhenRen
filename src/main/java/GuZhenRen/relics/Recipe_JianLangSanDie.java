package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.JianLangSanDie;
import GuZhenRen.cards.LangJian;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_JianLangSanDie extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_JianLangSanDie");
    private static final String IMG = "Recipe_JianDao.png";
    private static final String OUTLINE = "Recipe_JianDao.png";

    public Recipe_JianLangSanDie() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (浪剑+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(LangJian.ID); // 核心材料：浪剑
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(LangJian.ID)) {
            return true; // 必须升级过
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：泛型材料 (任意剑道蛊虫)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            // 判定 1: 必须有【剑道】标签
            if (!c.hasTag(GuZhenRenTags.JIAN_DAO)) {
                return false;
            }

            // 判定 2: 必须是蛊虫，且拥有 1-9 的转数
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
        // 返回合成好的杀招牌：剑浪三叠
        return new JianLangSanDie();
    }
}