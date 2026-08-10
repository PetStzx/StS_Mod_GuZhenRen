package GuZhenRen.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ChunQiuChanFadeEffect extends AbstractGameEffect {
    private static final float DURATION = 1.5f;

    public ChunQiuChanFadeEffect() {
        this.duration = DURATION;
        this.color = Color.WHITE.cpy();
        this.color.a = 1.0f;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.color.a = Math.max(0.0f, this.duration / DURATION);

        if (this.duration <= 0.0f) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.color.a > 0.0f) {
            sb.setColor(this.color);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0, 0, Settings.WIDTH, Settings.HEIGHT);
        }
    }

    @Override
    public void dispose() {
    }
}