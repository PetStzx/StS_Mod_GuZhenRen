package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AngryBird;
import GuZhenRen.cards.RongYanZhaLieGu;
import com.megacrit.cardcrawl.cards.AbstractCard;
import java.util.ArrayList;

public class Recipe_AngryBird extends AbstractRecipeRelic {
    public static final String ID = GuZhenRen.makeID("Recipe_AngryBird");
    private static final String IMG = "Recipe_YanDao.png";
    private static final String OUTLINE = "Recipe_YanDao.png";

    public Recipe_AngryBird() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        // 建议更新描述文本，提示玩家需要升级后的卡
        // 最好在 RelicStrings.json 里改成： "合成需求：1张 熔岩炸裂蛊+ 。"
        return DESCRIPTIONS[0];
    }

    @Override
    public ArrayList<String> getRequiredCardIDs() {
        ArrayList<String> list = new ArrayList<>();
        list.add(RongYanZhaLieGu.ID);
        return list;
    }

    // 【新增】指定熔岩炸裂蛊必须升级
    @Override
    public boolean requiresUpgrade(String cardID) {
        if (cardID.equals(RongYanZhaLieGu.ID)) {
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<String> getRequiredRelicIDs() {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getRewardCard() {
        return new AngryBird();
    }
}