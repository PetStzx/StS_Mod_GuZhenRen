package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AnQiSha;
import GuZhenRen.cards.FeiJian;
import GuZhenRen.cards.DieYingGu;
import GuZhenRen.cards.DuoChongJianYingGu;
import GuZhenRen.cards.JianYingGu;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class Recipe_AnQiSha extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_AnQiSha");
    private static final String IMG = "Recipe_JianDao.png";
    private static final String OUTLINE = "Recipe_JianDao.png";

    public Recipe_AnQiSha() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // =========================================================================
    //  步骤 1：固定材料 (飞剑+)
    // =========================================================================
    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(FeiJian.ID); // 第一个固定位：飞剑
        return list;
    }

    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(FeiJian.ID)) {
            return true; // 必须升级过
        }
        return false;
    }

    // =========================================================================
    //  步骤 2：特定范围材料 (剑影蛊 或 多重剑影蛊 或 叠影蛊)
    // =========================================================================
    @Override
    public int getIngredientCount() {
        return 2;
    }

    @Override
    public boolean isGenericIngredient(int index, AbstractCard c) {
        if (index == 1) {
            String id = c.cardID;
            // 只要是这三张牌中的任意一张即可
            return id.equals(JianYingGu.ID) ||
                    id.equals(DuoChongJianYingGu.ID) ||
                    id.equals(DieYingGu.ID);
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
        return new AnQiSha();
    }
}