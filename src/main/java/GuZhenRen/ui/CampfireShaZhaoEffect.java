package GuZhenRen.ui;

import GuZhenRen.cards.AbstractBenMingGuCard; // 【核心导入】用于防止崩溃
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
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.HashMap;

public class CampfireShaZhaoEffect extends AbstractGameEffect {
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

    private static final String CANCEL_TEXT = "返回";

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

                String msg = group.isEmpty() ? "没有可组并的杀招 (缺少配方或材料)" : "选择要组并的杀招";
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
        // 阶段 2：选择材料 (支持泛型判断)
        // =====================================================================
        else if (currentPhase == Phase.CHOOSE_INGREDIENTS) {

            if (!hasOpenedScreen) {
                // 【修改点】 使用 getIngredientCount() 获取总步骤数
                int totalIngredients = selectedRecipe.getIngredientCount();
                ArrayList<String> fixedIDs = selectedRecipe.getRequiredCardIDs();

                if (ingredientIndex < totalIngredients) {
                    CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    String msg = "";

                    // 情况 A: 固定 ID 的材料 (列表范围内的索引)
                    if (ingredientIndex < fixedIDs.size()) {
                        String targetID = fixedIDs.get(ingredientIndex);
                        boolean needUpgrade = selectedRecipe.requiresUpgrade(targetID);

                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            // 防止重复选择同一张牌
                            if (selectedIngredients.contains(c)) continue;

                            if (c.cardID.equals(targetID)) {
                                if (needUpgrade && !c.upgraded) continue;
                                group.addToTop(c);
                            }
                        }
                        msg = "选择材料: " + CardCrawlGame.languagePack.getCardStrings(targetID).NAME;
                        if (needUpgrade) msg += "+";
                    }
                    // 情况 B: 泛型材料 (超出列表范围的索引)
                    else {
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            // 防止重复选择同一张牌
                            if (selectedIngredients.contains(c)) continue;

                            // 调用遗物的判定方法
                            if (selectedRecipe.isGenericIngredient(ingredientIndex, c)) {
                                group.addToTop(c);
                            }
                        }
                        msg = "选择材料: " + selectedRecipe.getIngredientDescription(ingredientIndex);
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

                    // 索引增加，准备选下一个材料
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
        // 【核心修复】防止本命蛊移除时触发掉血导致 ConcurrentModificationException 崩溃
        // 1. 打开合成开关
        AbstractBenMingGuCard.isSynthesizing = true;

        // 2. 移除材料
        for (AbstractCard c : selectedIngredients) {
            AbstractDungeon.player.masterDeck.removeCard(c);
        }

        // 3. 关闭合成开关
        AbstractBenMingGuCard.isSynthesizing = false;

        // 4. 移除配方遗物并获得奖励卡牌
        AbstractDungeon.player.loseRelic(selectedRecipe.relicId);
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