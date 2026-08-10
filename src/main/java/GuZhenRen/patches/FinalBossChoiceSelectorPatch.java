package GuZhenRen.patches;

import GuZhenRen.GuZhenRen;
import GuZhenRen.enums.FinalBossChoice;
import GuZhenRen.util.FinalBossChoiceManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

public class FinalBossChoiceSelectorPatch {
    private static final Hitbox leftHb = new Hitbox(52.0F * Settings.scale, 52.0F * Settings.scale);
    private static final Hitbox rightHb = new Hitbox(52.0F * Settings.scale, 52.0F * Settings.scale);
    private static boolean initialized = false;
    private static AbstractPlayer.PlayerClass lastSelectedClass = null;

    private static boolean isOnCharSelect() {
        return CardCrawlGame.mainMenuScreen != null && CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.CHAR_SELECT;
    }

    private static AbstractPlayer.PlayerClass getSelectedSupportedClass(CharacterSelectScreen screen) {
        if (screen != null && screen.options != null) {
            for (CharacterOption option : screen.options) {
                if (option != null && option.selected && option.c != null) {
                    AbstractPlayer.PlayerClass playerClass = option.c.chosenClass;
                    return FinalBossChoiceManager.canChooseFinalBoss(playerClass) ? playerClass : null;
                }
            }
        }
        return null;
    }

    private static void updateSelectedClass(AbstractPlayer.PlayerClass selectedClass) {
        if (lastSelectedClass != selectedClass) {
            lastSelectedClass = selectedClass;
        }
    }

    private static void ensureInit(CharacterSelectScreen screen) {
        if (!initialized) {
            updateHitboxes(screen);
            initialized = true;
        }
    }

    private static void updateHitboxes(CharacterSelectScreen screen) {
        float[] anchor = getAnchor(screen);
        float centerX = anchor[0];
        float baseY = anchor[1];
        leftHb.move(centerX - 118.0F * Settings.scale, baseY);
        rightHb.move(centerX + 118.0F * Settings.scale, baseY);
        leftHb.update();
        rightHb.update();
    }

    private static float[] getAnchor(CharacterSelectScreen screen) {
        float centerX = Settings.WIDTH * 0.5F;
        float baseY = Settings.HEIGHT * 0.19F;
        if (screen != null && screen.confirmButton != null && screen.confirmButton.hb != null) {
            centerX = screen.confirmButton.hb.cX - 110.0F * Settings.scale;
            baseY = screen.confirmButton.hb.cY + 148.0F * Settings.scale;
        }
        centerX = Math.max(220.0F * Settings.scale, Math.min(Settings.WIDTH - 220.0F * Settings.scale, centerX));
        baseY = Math.max(95.0F * Settings.scale, Math.min(Settings.HEIGHT - 160.0F * Settings.scale, baseY));
        return new float[]{centerX, baseY};
    }

    private static void cycle(int delta) {
        FinalBossChoice[] vals = FinalBossChoice.values();
        FinalBossChoice current = FinalBossChoiceManager.getMenuChoice();
        int idx = current.ordinal();
        int next = (idx + delta + vals.length) % vals.length;

        FinalBossChoiceManager.saveGlobalConfig(vals[next]);
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "render")
    public static class RenderPatch {
        @SpirePostfixPatch
        public static void Postfix(CharacterSelectScreen __instance, SpriteBatch sb) {
            if (isOnCharSelect()) {
                AbstractPlayer.PlayerClass selectedClass = getSelectedSupportedClass(__instance);
                if (selectedClass != null) {
                    updateSelectedClass(selectedClass);
                    ensureInit(__instance);

                    float[] anchor = getAnchor(__instance);
                    float centerX = anchor[0];
                    float baseY = anchor[1];

                    UIStrings ui = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("FinalBossChoiceUI"));
                    String title = (ui != null && ui.TEXT.length > 0) ? ui.TEXT[0] : "End Boss";
                    String heart = (ui != null && ui.TEXT.length > 1) ? ui.TEXT[1] : "Heart";
                    String longGong = (ui != null && ui.TEXT.length > 2) ? ui.TEXT[2] : "Long Gong";
                    String random = (ui != null && ui.TEXT.length > 3) ? ui.TEXT[3] : "Random";

                    FinalBossChoice choice = FinalBossChoiceManager.getMenuChoice();
                    String choiceText = choice == FinalBossChoice.HEART ? heart : (choice == FinalBossChoice.LONG_GONG ? longGong : random);

                    FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, title, centerX, baseY + 44.0F * Settings.scale, Settings.GOLD_COLOR);
                    FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, choiceText, centerX, baseY, Settings.CREAM_COLOR);

                    Color leftColor = leftHb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR;
                    Color rightColor = rightHb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR;

                    FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, "<", leftHb.cX, leftHb.cY + 4.0F * Settings.scale, leftColor);
                    FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, ">", rightHb.cX, rightHb.cY + 4.0F * Settings.scale, rightColor);

                    leftHb.render(sb);
                    rightHb.render(sb);
                }
            }
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "update")
    public static class UpdatePatch {
        @SpirePostfixPatch
        public static void Postfix(CharacterSelectScreen __instance) {
            if (isOnCharSelect()) {
                AbstractPlayer.PlayerClass selectedClass = getSelectedSupportedClass(__instance);
                if (selectedClass != null) {
                    updateSelectedClass(selectedClass);
                    ensureInit(__instance);
                    updateHitboxes(__instance);

                    if (InputHelper.justClickedLeft) {
                        if (leftHb.hovered) {
                            cycle(-1);
                            leftHb.clickStarted = true;
                            CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
                        } else if (rightHb.hovered) {
                            cycle(1);
                            rightHb.clickStarted = true;
                            CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
                        }
                    }
                }
            }
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "open")
    public static class OpenPatch {
        @SpirePostfixPatch
        public static void Postfix(CharacterSelectScreen __instance, boolean isTrial) {
            initialized = false;
            lastSelectedClass = null;
        }
    }
}