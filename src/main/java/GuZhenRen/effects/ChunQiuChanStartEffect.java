package GuZhenRen.effects;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ScreenUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ChunQiuChanStartEffect extends AbstractGameEffect {
    private static final float DURATION = 3.0f;
    private static ShaderProgram shader = null;
    private TextureRegion screenTexture = null;
    private final Runnable onComplete;
    private float timePassed = 0.0f;

    public ChunQiuChanStartEffect(Runnable onComplete) {
        this.onComplete = onComplete;
        this.duration = DURATION;
        this.color = Color.WHITE.cpy();

        captureScreen();
        initShader();
    }

    private void captureScreen() {
        try {
            // 读取当前全屏画面
            screenTexture = ScreenUtils.getFrameBufferTexture(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            // 【修好画面颠倒】：删除了之前导致翻转的 screenTexture.flip(false, true);
        } catch (Exception e) {
            GuZhenRen.logger.error("春秋蝉画面截图失败: " + e.getMessage());
        }
    }

    private void initShader() {
        if (shader == null) {
            ShaderProgram.pedantic = false;

            String vsh = Gdx.files.internal(GuZhenRen.assetPath("shaders/ChunQiuChan.vsh")).readString();
            String fsh = Gdx.files.internal(GuZhenRen.assetPath("shaders/ChunQiuChan.fsh")).readString();

            shader = new ShaderProgram(vsh, fsh);

            if (!shader.isCompiled()) {
                GuZhenRen.logger.error("春秋蝉 Shader 编译失败:\n" + shader.getLog());
            }
        }
    }

    @Override
    public void update() {
        timePassed += Gdx.graphics.getDeltaTime();
        this.duration -= Gdx.graphics.getDeltaTime();

        if (this.duration <= 0.0f) {
            this.isDone = true;
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (shader != null && shader.isCompiled() && screenTexture != null) {
            sb.setShader(shader);

            shader.setUniformf("u_time", timePassed);
            shader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            sb.setColor(Color.WHITE);
            sb.draw(screenTexture, 0, 0, Settings.WIDTH, Settings.HEIGHT);

            sb.setShader(null);
        }
    }

    @Override
    public void dispose() {
        if (screenTexture != null && screenTexture.getTexture() != null) {
            screenTexture.getTexture().dispose();
            screenTexture = null;
        }
    }
}
