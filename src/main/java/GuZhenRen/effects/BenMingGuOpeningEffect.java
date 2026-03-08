package GuZhenRen.effects;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.LiLiangGu;
import GuZhenRen.cards.ZhiHuiGu;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings; // 【新增】
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Collections;

public class BenMingGuOpeningEffect extends AbstractGameEffect {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("BenMingGuScreen"));
    public static final String[] TEXT = uiStrings.TEXT;

    private boolean screenOpened = false;
    private boolean isTextHidden = false;

    public BenMingGuOpeningEffect() {
        this.duration = 0.5F;
        this.startingDuration = 0.5F;
    }

    @Override
    public void update() {
        if (this.duration > 0.0F) {
            this.duration -= Gdx.graphics.getDeltaTime();
            return;
        }

        if (!this.screenOpened) {
            openSelectionScreen();
            this.screenOpened = true;
            return;
        }

        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.CARD_REWARD) {
            this.isTextHidden = false;
        } else {
            updateHideState();
        }

        if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
            AbstractCard selected = AbstractDungeon.cardRewardScreen.discoveryCard;

            if (selected.cardID.equals(LiLiangGu.ID) || selected.cardID.equals(ZhiHuiGu.ID)) {
                int hpLoss = (int)(AbstractDungeon.player.maxHealth * 0.33F);
                if (hpLoss < 1) hpLoss = 1;

                CardCrawlGame.sound.play("BLUNT_HEAVY");
                AbstractDungeon.effectList.add(new BorderFlashEffect(Color.RED));
                AbstractDungeon.player.decreaseMaxHealth(hpLoss);
            }

            AbstractCard realCard = CardLibrary.getCard(selected.cardID).makeCopy();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(realCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

            AbstractDungeon.cardRewardScreen.discoveryCard = null;
            this.isDone = true;
        }
    }

    private void updateHideState() {
        try {
            Object peekBtnObj = ReflectionHacks.getPrivate(
                    AbstractDungeon.cardRewardScreen,
                    CardRewardScreen.class,
                    "peekButton"
            );

            if (peekBtnObj instanceof PeekButton) {
                PeekButton btn = (PeekButton) peekBtnObj;
                if (btn.hb.hovered) {
                    if (InputHelper.justReleasedClickLeft) {
                        this.isTextHidden = !this.isTextHidden;
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void openSelectionScreen() {
        ArrayList<AbstractCard> pool = new ArrayList<>();

        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c.color == CardColorEnum.GUZHENREN_GREY && c.hasTag(GuZhenRenTags.BEN_MING_GU)) {
                pool.add(c.makeCopy());
            }
        }

        java.util.Random independentRng = new java.util.Random(Settings.seed);
        Collections.shuffle(pool, independentRng);

        ArrayList<AbstractCard> choices = new ArrayList<>();
        int count = Math.min(pool.size(), 3);

        for (int i = 0; i < count; i++) {
            AbstractCard c = pool.get(i);
            c.applyPowers();
            c.initializeDescription();
            choices.add(c);
        }

        AbstractDungeon.cardRewardScreen.customCombatOpen(choices, TEXT[0], false);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.screenOpened && !this.isDone && AbstractDungeon.cardRewardScreen.rewardGroup != null) {

            if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.CARD_REWARD) {
                return;
            }

            if (this.isTextHidden) {
                return;
            }

            for (AbstractCard c : AbstractDungeon.cardRewardScreen.rewardGroup) {
                if (c.cardID.equals(LiLiangGu.ID) || c.cardID.equals(ZhiHuiGu.ID)) {
                    float textX = c.current_x;
                    float textY = c.current_y - (AbstractCard.IMG_HEIGHT * c.drawScale / 2.0f) - (20.0f * Settings.scale);

                    FontHelper.renderFontCentered(
                            sb,
                            FontHelper.topPanelInfoFont,
                            TEXT[1],
                            textX,
                            textY,
                            Settings.RED_TEXT_COLOR
                    );
                }
            }
        }
    }

    @Override
    public void dispose() { }
}