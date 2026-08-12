package GuZhenRen.patches;

import GuZhenRen.relics.AbstractRecipeRelic;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import java.util.ArrayList;

public class KillerMoveRelicRowPatch {
    private static final float START_X = 64.0F;
    private static final float DESKTOP_START_Y = 102.0F;
    private static final float MOBILE_START_Y = 132.0F;
    private static final float ROW_GAP = 72.0F;
    private static int recipeRelicPage = 0;
    private static Hitbox recipeLeftScrollHb;
    private static Hitbox recipeRightScrollHb;

    private static boolean hasRecipeRelics() {
        if (AbstractDungeon.player == null) {
            return false;
        }

        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof AbstractRecipeRelic) {
                return true;
            }
        }
        return false;
    }

    private static int getNormalRelicCount() {
        int count = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (!(relic instanceof AbstractRecipeRelic)) {
                count++;
            }
        }
        return count;
    }

    private static int getRecipeRelicCount() {
        int count = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof AbstractRecipeRelic) {
                count++;
            }
        }
        return count;
    }

    private static int getNormalRelicIndex(AbstractRelic target) {
        int index = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof AbstractRecipeRelic) {
                continue;
            }
            if (relic == target) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private static int getRecipeRelicIndex(AbstractRelic target) {
        int index = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (!(relic instanceof AbstractRecipeRelic)) {
                continue;
            }
            if (relic == target) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private static float getTopRowY() {
        float yOffset = Settings.isMobile ? MOBILE_START_Y : DESKTOP_START_Y;
        return Settings.HEIGHT - yOffset * Settings.scale;
    }

    private static int getMaxPage(int relicCount) {
        return relicCount == 0
                ? 0
                : (relicCount - 1) / AbstractRelic.MAX_RELICS_PER_PAGE;
    }

    // In Endless Mode the player accumulates Blight items, which the base game
    // renders in the same row (and starting X) as our killer-move recipe row.
    // Instead of pinning the Blights on the left, we treat the second row as a
    // single shared, paginated sequence: [ Blights..., recipe relics... ]. The
    // Blights occupy the first slots of the sequence, recipe relics follow, and
    // paging scrolls through the whole sequence (Blights included), exactly like
    // the normal relic row. The left arrow always sits at the far left.
    private static int getBlightCount() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.blights == null) {
            return 0;
        }
        return AbstractDungeon.player.blights.size();
    }

    private static int getSecondRowCount() {
        return getBlightCount() + getRecipeRelicCount();
    }

    private static int getSecondRowMaxPage() {
        int count = getSecondRowCount();
        return count == 0 ? 0 : (count - 1) / AbstractRelic.MAX_RELICS_PER_PAGE;
    }

    // Left edge of the second row. Kept fixed (left-aligned) regardless of how
    // many pages the sequence spans, so it always lines up with the first relic
    // row and the Blights' default start position.
    private static float getSecondRowStartX() {
        return START_X * Settings.scale;
    }

    private static float getSecondRowY() {
        return getTopRowY() - ROW_GAP * Settings.scale;
    }

    private static void updateRecipeArrowPositions() {
        if (recipeLeftScrollHb == null) {
            recipeLeftScrollHb = new Hitbox(64.0F * Settings.scale, 64.0F * Settings.scale);
            recipeRightScrollHb = new Hitbox(64.0F * Settings.scale, 64.0F * Settings.scale);
        }

        float recipeRowY = getSecondRowY();
        recipeLeftScrollHb.move(32.0F * Settings.scale, recipeRowY);
        recipeRightScrollHb.move(Settings.WIDTH - 32.0F * Settings.scale, recipeRowY);
    }

    private static void positionRelic(AbstractRelic relic) {
        if (AbstractDungeon.player == null) {
            return;
        }

        boolean recipeRelic = relic instanceof AbstractRecipeRelic;
        int logicalIndex = recipeRelic
                ? getRecipeRelicIndex(relic)
                : getNormalRelicIndex(relic);
        if (logicalIndex < 0) {
            return;
        }

        Float offsetX = ReflectionHacks.getPrivateStatic(AbstractRelic.class, "offsetX");
        float visibleY = getTopRowY();
        float visibleX;
        if (recipeRelic) {
            int perPage = AbstractRelic.MAX_RELICS_PER_PAGE;
            int combinedIndex = getBlightCount() + logicalIndex;
            if (!relic.isDone) {
                recipeRelicPage = combinedIndex / perPage;
            }
            // Only lay out recipes that belong to the current second-row page;
            // off-page recipes are neither rendered nor hoverable.
            if (combinedIndex / perPage != recipeRelicPage) {
                return;
            }
            int column = combinedIndex % perPage;
            visibleX = getSecondRowStartX() + column * AbstractRelic.PAD_X;
            visibleY = getSecondRowY();
        } else {
            int column = logicalIndex % AbstractRelic.MAX_RELICS_PER_PAGE;
            visibleX = (START_X * Settings.scale) + column * AbstractRelic.PAD_X;
            if (getNormalRelicCount() > AbstractRelic.MAX_RELICS_PER_PAGE) {
                visibleX += 36.0F * Settings.scale;
            }
        }

        relic.targetX = visibleX - offsetX;
        relic.targetY = visibleY;
        if (relic.isDone) {
            relic.currentX = relic.targetX;
            relic.currentY = relic.targetY;
            relic.hb.move(visibleX, visibleY);
        }
    }

    private static void positionAllRelics() {
        if (AbstractDungeon.player == null) {
            return;
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            positionRelic(relic);
        }
    }

    private static void updateArrow(
            Hitbox hitbox,
            boolean visible,
            int pageChange,
            boolean recipeRow
    ) {
        if (!visible) {
            hitbox.unhover();
            return;
        }

        hitbox.update();
        if (hitbox.justHovered) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }
        if (hitbox.hovered && InputHelper.justClickedLeft) {
            hitbox.clickStarted = true;
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
        }
        if (hitbox.clicked) {
            hitbox.clicked = false;
            CardCrawlGame.sound.playA("DECK_OPEN", -0.1F);
            if (recipeRow) {
                recipeRelicPage += pageChange;
            } else {
                AbstractRelic.relicPage += pageChange;
            }
            positionAllRelics();
        }
    }

    private static void renderArrow(SpriteBatch sb, Hitbox hitbox, boolean left) {
        sb.draw(
                left ? ImageMaster.CF_LEFT_ARROW : ImageMaster.CF_RIGHT_ARROW,
                hitbox.cX - 24.0F,
                hitbox.cY - 24.0F,
                24.0F,
                24.0F,
                48.0F,
                48.0F,
                Settings.scale,
                Settings.scale,
                0.0F,
                0,
                0,
                48,
                48,
                false,
                false
        );
        hitbox.render(sb);
    }

    @SpirePatch(clz = AbstractRelic.class, method = "update")
    public static class RelicUpdatePatch {
        private static int originalPage;

        @SpirePrefixPatch
        public static void Prefix(AbstractRelic __instance) {
            originalPage = AbstractRelic.relicPage;
            if (!hasRecipeRelics()) {
                return;
            }

            positionRelic(__instance);

            int actualIndex = AbstractDungeon.player.relics.indexOf(__instance);
            if (__instance instanceof AbstractRecipeRelic) {
                int combinedIndex = getBlightCount() + getRecipeRelicIndex(__instance);
                AbstractRelic.relicPage =
                        combinedIndex / AbstractRelic.MAX_RELICS_PER_PAGE == recipeRelicPage
                                ? actualIndex / AbstractRelic.MAX_RELICS_PER_PAGE
                                : -1;
                return;
            }
            int normalIndex = getNormalRelicIndex(__instance);
            AbstractRelic.relicPage = normalIndex / AbstractRelic.MAX_RELICS_PER_PAGE == originalPage
                    ? actualIndex / AbstractRelic.MAX_RELICS_PER_PAGE
                    : -1;
        }

        @SpirePostfixPatch
        public static void Postfix(AbstractRelic __instance) {
            AbstractRelic.relicPage = originalPage;
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "renderRelics")
    public static class PlayerRenderRelicsPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractPlayer __instance, SpriteBatch sb) {
            if (!hasRecipeRelics()) {
                return SpireReturn.Continue();
            }

            ArrayList<AbstractRelic> visibleRelics = new ArrayList<>();
            int normalIndex = 0;
            int recipeIndex = 0;
            int blightCount = getBlightCount();
            for (AbstractRelic relic : __instance.relics) {
                if (relic instanceof AbstractRecipeRelic) {
                    int combinedIndex = blightCount + recipeIndex;
                    if (combinedIndex / AbstractRelic.MAX_RELICS_PER_PAGE == recipeRelicPage) {
                        relic.renderInTopPanel(sb);
                        visibleRelics.add(relic);
                    }
                    recipeIndex++;
                } else {
                    if (normalIndex / AbstractRelic.MAX_RELICS_PER_PAGE == AbstractRelic.relicPage) {
                        relic.renderInTopPanel(sb);
                        visibleRelics.add(relic);
                    }
                    normalIndex++;
                }
            }

            for (AbstractRelic relic : visibleRelics) {
                if (relic.hb.hovered) {
                    relic.renderTip(sb);
                }
            }
            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "updateRelics")
    public static class TopPanelUpdateRelicsPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(
                TopPanel __instance,
                Hitbox ___leftScrollHb,
                Hitbox ___rightScrollHb
        ) {
            if (!hasRecipeRelics()) {
                return SpireReturn.Continue();
            }

            int normalRelicCount = getNormalRelicCount();
            int maxNormalPage = getMaxPage(normalRelicCount);
            if (AbstractRelic.relicPage > maxNormalPage) {
                AbstractRelic.relicPage = maxNormalPage;
            }

            int maxRecipePage = getSecondRowMaxPage();
            if (recipeRelicPage > maxRecipePage) {
                recipeRelicPage = maxRecipePage;
            }

            updateRecipeArrowPositions();
            updateArrow(___leftScrollHb, AbstractRelic.relicPage > 0, -1, false);
            updateArrow(___rightScrollHb, AbstractRelic.relicPage < maxNormalPage, 1, false);
            updateArrow(recipeLeftScrollHb, recipeRelicPage > 0, -1, true);
            updateArrow(recipeRightScrollHb, recipeRelicPage < maxRecipePage, 1, true);
            positionAllRelics();
            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "adjustRelicHbs")
    public static class TopPanelAdjustRelicHitboxesPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix() {
            if (!hasRecipeRelics()) {
                return SpireReturn.Continue();
            }
            positionAllRelics();
            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "renderRelics")
    public static class TopPanelRenderRelicsPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(
                TopPanel __instance,
                SpriteBatch sb,
                Hitbox ___leftScrollHb,
                Hitbox ___rightScrollHb
        ) {
            if (!hasRecipeRelics()) {
                return SpireReturn.Continue();
            }

            AbstractDungeon.player.renderRelics(sb);
            sb.setColor(Color.WHITE);
            updateRecipeArrowPositions();

            int normalRelicCount = getNormalRelicCount();
            int maxNormalPage = getMaxPage(normalRelicCount);
            if (AbstractRelic.relicPage > 0) {
                renderArrow(sb, ___leftScrollHb, true);
            }
            if (AbstractRelic.relicPage < maxNormalPage) {
                renderArrow(sb, ___rightScrollHb, false);
            }

            int maxRecipePage = getSecondRowMaxPage();
            if (recipeRelicPage > 0) {
                renderArrow(sb, recipeLeftScrollHb, true);
            }
            if (recipeRelicPage < maxRecipePage) {
                renderArrow(sb, recipeRightScrollHb, false);
            }
            return SpireReturn.Return(null);
        }
    }

    // Fold Endless-Mode Blights into the shared second-row sequence. Blights come
    // first in the sequence, so on the current page they render alongside the
    // recipe relics; Blights that fall on another page are moved off-screen so
    // they are neither drawn (base game renders them at currentX/currentY) nor
    // hoverable. When no recipe relic is owned, Blights keep their vanilla layout.
    @SpirePatch(clz = AbstractBlight.class, method = "update")
    public static class BlightUpdatePatch {
        private static final float OFFSCREEN_X = -9999.0F;

        @SpirePrefixPatch
        public static void Prefix(AbstractBlight __instance) {
            if (!hasRecipeRelics() || AbstractDungeon.player == null) {
                return;
            }

            int combinedIndex = AbstractDungeon.player.blights.indexOf(__instance);
            if (combinedIndex < 0) {
                return;
            }

            int perPage = AbstractRelic.MAX_RELICS_PER_PAGE;
            if (!__instance.isDone) {
                recipeRelicPage = combinedIndex / perPage;
            }

            float y = getSecondRowY();
            if (combinedIndex / perPage == recipeRelicPage) {
                int column = combinedIndex % perPage;
                float x = getSecondRowStartX() + column * AbstractRelic.PAD_X;
                __instance.targetX = x;
                __instance.targetY = y;
                if (__instance.isDone) {
                    __instance.currentX = x;
                    __instance.currentY = y;
                    __instance.hb.move(x, y);
                }
            } else {
                // Off-page: park the Blight (and its hitbox) off-screen.
                __instance.targetX = OFFSCREEN_X;
                __instance.currentX = OFFSCREEN_X;
                __instance.targetY = y;
                __instance.currentY = y;
                __instance.hb.move(OFFSCREEN_X, y);
            }
        }
    }
}
