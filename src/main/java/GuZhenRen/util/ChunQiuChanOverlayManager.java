package GuZhenRen.util;

import basemod.BaseMod;
import basemod.interfaces.PostRenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class ChunQiuChanOverlayManager implements PostRenderSubscriber {
    private static ChunQiuChanOverlayManager instance;
    private static float whiteOverlayAlpha = 0.0f;
    private static boolean isFadingOutWhite = false;

    private static void init() {
        if (instance == null) {
            instance = new ChunQiuChanOverlayManager();
            BaseMod.subscribe(instance);
        }
    }

    public static void startOverlay() {
        init();
        whiteOverlayAlpha = 1.0f;
        isFadingOutWhite = true;
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (whiteOverlayAlpha > 0.0f) {
            boolean needEnd = false;
            if (!sb.isDrawing()) {
                sb.begin();
                needEnd = true;
            }

            sb.setColor(1.0f, 1.0f, 1.0f, whiteOverlayAlpha);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0, 0, Settings.WIDTH, Settings.HEIGHT);

            if (needEnd) {
                sb.end();
            }

            if (isFadingOutWhite && !CardCrawlGame.loadingSave && CardCrawlGame.mode == CardCrawlGame.GameMode.GAMEPLAY) {
                whiteOverlayAlpha -= Gdx.graphics.getDeltaTime() / 1.5f;
                if (whiteOverlayAlpha <= 0.0f) {
                    whiteOverlayAlpha = 0.0f;
                    isFadingOutWhite = false;
                }
            }
        }
    }
}