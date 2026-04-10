package GuZhenRen.ui;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractBenMingGuCard;
import GuZhenRen.cards.FangWeiGu;
import GuZhenRen.relics.AbstractRecipeRelic;
import GuZhenRen.util.ShaZhaoHelper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.HashMap;

public class CampfireShaZhaoEffect extends AbstractGameEffect {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("AssembleOption"));
    public static final String[] TEXT = uiStrings.TEXT;

    private boolean hasOpenedScreen = false;
    private Color screenColor = AbstractDungeon.fadeColor.cpy();

    private enum Phase {
        CHOOSE_RECIPE,
        CHOOSE_INGREDIENTS,
        COMPLETE
    }
    private Phase currentPhase = Phase.CHOOSE_RECIPE;

    private ArrayList<AbstractRecipeRelic> craftableRecipes;
    private AbstractRecipeRelic selectedRecipe;
    private HashMap<String, AbstractRecipeRelic> cardIdToRecipeMap = new HashMap<>();

    private int ingredientIndex = 0;
    private ArrayList<AbstractCard> selectedIngredients = new ArrayList<>();

    private static final String CANCEL_TEXT = TEXT[3];

    public CampfireShaZhaoEffect() {
        this.duration = 1.5F;
        this.screenColor.a = 0.0F;

        this.craftableRecipes = ShaZhaoHelper.getCraftableRecipes();

        for (AbstractRecipeRelic r : craftableRecipes) {
            AbstractCard reward = r.getRewardCard();
            reward.initializeDescription();
            cardIdToRecipeMap.put(reward.cardID, r);
        }
    }

    @Override
    public void update() {
        if (!AbstractDungeon.isScreenUp) {
            this.duration -= Gdx.graphics.getDeltaTime();
            updateBlackScreenColor();
        }

        if (this.isDone) {
            return;
        }

        // =====================================================================
        // 阶段 1：选择配方
        // =====================================================================
        if (currentPhase == Phase.CHOOSE_RECIPE) {
            if (!hasOpenedScreen) {
                CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractRecipeRelic r : craftableRecipes) {
                    group.addToTop(r.getRewardCard());
                }

                String msg = group.isEmpty() ? TEXT[4] : TEXT[5];
                AbstractDungeon.gridSelectScreen.open(group, 1, msg, false, false, true, false);
                AbstractDungeon.overlayMenu.cancelButton.show(CANCEL_TEXT);
                hasOpenedScreen = true;
            }
            else if (!AbstractDungeon.isScreenUp) {
                if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                    AbstractCard selected = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();

                    this.selectedRecipe = cardIdToRecipeMap.get(selected.cardID);
                    AbstractDungeon.overlayMenu.cancelButton.hide();
                    this.currentPhase = Phase.CHOOSE_INGREDIENTS;
                    this.hasOpenedScreen = false;
                } else {
                    cancelProcess();
                }
            }
        }

        // =====================================================================
        // 阶段 2：选择材料
        // =====================================================================
        else if (currentPhase == Phase.CHOOSE_INGREDIENTS) {

            if (!hasOpenedScreen) {
                int totalIngredients = selectedRecipe.getIngredientCount();
                ArrayList<String> fixedIDs = selectedRecipe.getRequiredCardIDs();

                if (ingredientIndex < totalIngredients) {
                    CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    String msg = "";

                    if (ingredientIndex < fixedIDs.size()) {
                        String targetID = fixedIDs.get(ingredientIndex);
                        boolean needUpgrade = selectedRecipe.requiresUpgrade(targetID);

                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (selectedIngredients.contains(c)) continue;

                            boolean isUniversalMaterial = c.cardID.equals(FangWeiGu.ID) && c.upgraded;

                            if (c.cardID.equals(targetID) || isUniversalMaterial) {
                                if (needUpgrade && !c.upgraded && !isUniversalMaterial) continue;
                                group.addToTop(c);
                            }
                        }
                        msg = String.format(TEXT[6], CardCrawlGame.languagePack.getCardStrings(targetID).NAME);
                        if (needUpgrade) msg += "+";
                    }
                    else {
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (selectedIngredients.contains(c)) continue;

                            boolean isUniversalMaterial = c.cardID.equals(FangWeiGu.ID) && c.upgraded;

                            if (selectedRecipe.isGenericIngredient(ingredientIndex, c) || isUniversalMaterial) {
                                group.addToTop(c);
                            }
                        }
                        msg = String.format(TEXT[6], selectedRecipe.getIngredientDescription(ingredientIndex));
                    }

                    AbstractDungeon.gridSelectScreen.open(group, 1, msg, false, false, true, false);
                    AbstractDungeon.overlayMenu.cancelButton.show(CANCEL_TEXT);
                    hasOpenedScreen = true;
                } else {
                    completeSynthesis();
                    this.currentPhase = Phase.COMPLETE;
                }
            }
            else if (!AbstractDungeon.isScreenUp) {
                if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                    AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                    selectedIngredients.add(c);
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();
                    AbstractDungeon.overlayMenu.cancelButton.hide();

                    ingredientIndex++;
                    hasOpenedScreen = false;
                } else {
                    cancelProcess();
                }
            }
        }

        // =====================================================================
        // 阶段 3：完成
        // =====================================================================
        if (currentPhase == Phase.COMPLETE) {
            if (this.duration < 1.0F) {
                this.isDone = true;
                AbstractDungeon.overlayMenu.cancelButton.hide();
                backToCampfire();
            }
        }
    }

    private void cancelProcess() {
        this.isDone = true;
        AbstractDungeon.overlayMenu.cancelButton.hide();
        backToCampfire();
    }

    private void backToCampfire() {
        if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
            RestRoom r = (RestRoom) AbstractDungeon.getCurrRoom();
            r.fadeIn();
            r.campfireUI.reopen();
        }
    }

    private void completeSynthesis() {
        AbstractBenMingGuCard.isSynthesizing = true;

        for (AbstractCard c : selectedIngredients) {
            AbstractDungeon.player.masterDeck.removeCard(c);
        }

        AbstractBenMingGuCard.isSynthesizing = false;

        AbstractRecipeRelic relicToExhaust = (AbstractRecipeRelic) AbstractDungeon.player.getRelic(selectedRecipe.relicId);
        if (relicToExhaust != null) {
            relicToExhaust.usedUp();
            relicToExhaust.grayscale = true;
        }

        AbstractCard reward = selectedRecipe.getRewardCard();
        reward.initializeDescription();

        AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(reward, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
        CardCrawlGame.sound.play("UNLOCK_PING");
    }

    private void updateBlackScreenColor() {
        if (this.duration < 1.0F) {
            this.screenColor.a = this.duration;
        } else {
            this.screenColor.a = 1.0F;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!AbstractDungeon.isScreenUp) {
            sb.setColor(this.screenColor);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT);
        }
    }

    @Override
    public void dispose() {}
}