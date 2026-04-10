package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.ShaZhaoHelper;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.RestRoom;

import java.util.ArrayList;

public abstract class AbstractRecipeRelic extends CustomRelic {

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("RecipeUI"));
    public static final String[] TEXT = uiStrings.TEXT;
    protected AbstractCard previewCard;
    private float checkTimer = 0.5f;

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

    public AbstractCard getPreviewCard() {
        if (previewCard == null) {
            previewCard = getRewardCard();
            this.addTips(previewCard);
            previewCard.drawScale = 0.98f;
            previewCard.targetDrawScale = 0.98f;
        }
        return previewCard;
    }

    @Override
    public void update() {
        super.update();
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(this.relicId)) {
            if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
                checkTimer -= Gdx.graphics.getDeltaTime();
                if (checkTimer > 0.0f) return;
                checkTimer = 0.5f;

                if (ShaZhaoHelper.canCraft(this)) {
                    this.beginLongPulse();
                } else {
                    this.stopPulse();
                }
            } else {
                this.stopPulse();
            }
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);

        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(this.relicId) && !CardCrawlGame.relicPopup.isOpen) {
            AbstractCard card = getPreviewCard();
            if (card != null) {
                float drawX, drawY;
                if ((float) InputHelper.mX < 1400.0F * Settings.scale) {
                    drawX = (float) InputHelper.mX + 60.0F * Settings.scale;
                    drawY = (float) InputHelper.mY - 30.0F * Settings.scale;
                } else {
                    drawX = (float) InputHelper.mX - 350.0F * Settings.scale;
                    drawY = (float) InputHelper.mY - 50.0F * Settings.scale;
                }
                float totalH = 0;
                float edge = 32.0F * Settings.scale;

                for (PowerTip tip : this.tips) {
                    float textH = -FontHelper.getSmartHeight(
                            FontHelper.tipBodyFont,
                            tip.body,
                            280.0F * Settings.scale,
                            26.0F * Settings.scale)
                            - 7.0F * Settings.scale;

                    totalH += textH + edge * 3.15F;
                }

                float targetX = drawX + (160.0F * Settings.scale);
                float cardHeight = AbstractCard.IMG_HEIGHT * card.drawScale;
                float targetY = drawY - totalH - (cardHeight / 2.0F) + 20.0f * Settings.scale;

                card.current_x = targetX;
                card.target_x = targetX;
                card.current_y = targetY;
                card.target_y = targetY;

                card.update();
                card.render(sb);
                if (card.cardsToPreview != null) {
                    card.renderCardPreview(sb);
                }
            }
        }
    }

    protected void addTips(AbstractCard previewCard) {
        for (String keyword : previewCard.keywords) {
            for (String allowKey : getTipKeywords()) {
                if (keyword.contains(allowKey)) {
                    this.tips.add(new PowerTip(TipHelper.capitalize(keyword), GameDictionary.keywords.get(keyword)));
                }
            }
        }
    }

    protected String[] getTipKeywords() {
        return new String[0];
    }
}