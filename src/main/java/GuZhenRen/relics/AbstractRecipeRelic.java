package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings; // 【新增】

import java.util.ArrayList;

public abstract class AbstractRecipeRelic extends CustomRelic {

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("RecipeUI"));
    public static final String[] TEXT = uiStrings.TEXT;

    public AbstractRecipeRelic(String id, String imgName, String outlineName, RelicTier tier, LandingSound sfx) {
        super(id,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + imgName)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + outlineName)),
                tier, sfx);
    }

    public abstract ArrayList<String> getRequiredCardIDs();

    public abstract ArrayList<String> getRequiredRelicIDs();

    public abstract AbstractCard getRewardCard();

    public boolean requiresUpgrade(String cardID) {
        return false;
    }

    public int getIngredientCount() {
        return getRequiredCardIDs().size();
    }

    public boolean isGenericIngredient(int index, AbstractCard c) {
        return false;
    }

    public String getIngredientDescription(int index) {
        return TEXT[0];
    }
}