package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import java.util.ArrayList;

public abstract class AbstractRecipeRelic extends CustomRelic {

    public AbstractRecipeRelic(String id, String imgName, String outlineName, RelicTier tier, LandingSound sfx) {
        super(id,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + imgName)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + outlineName)),
                tier, sfx);
    }

    public abstract ArrayList<String> getRequiredCardIDs(); // 返回固定ID的列表

    public abstract ArrayList<String> getRequiredRelicIDs();

    public abstract AbstractCard getRewardCard();

    public boolean requiresUpgrade(String cardID) {
        return false;
    }

    // =========================================================================
    //  【新增】 支持泛型材料的核心方法
    // =========================================================================

    /**
     * 获取总共需要的材料卡牌数量。
     * 默认为固定ID列表的长度。如果有泛型材料，子类必须重写此方法返回更大的数。
     */
    public int getIngredientCount() {
        return getRequiredCardIDs().size();
    }

    /**
     * 检查某张卡是否符合第 index 步的泛型材料要求。
     * 仅当 index 超出 getRequiredCardIDs() 范围时调用。
     */
    public boolean isGenericIngredient(int index, AbstractCard c) {
        return false;
    }

    /**
     * 获取第 index 步的提示文本（用于UI显示）。
     */
    public String getIngredientDescription(int index) {
        return "未知材料";
    }
}