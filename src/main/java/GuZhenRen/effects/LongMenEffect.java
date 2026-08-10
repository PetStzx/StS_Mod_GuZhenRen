package GuZhenRen.effects;

import GuZhenRen.GuZhenRen;
import GuZhenRen.monsters.LongGong;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.EmptyStanceEffect;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;
import com.megacrit.cardcrawl.vfx.combat.SanctityEffect;

public class LongMenEffect extends AbstractGameEffect {
    private Texture img;

    private TextureRegion gateRegion;
    private TextureRegion shadowRegion;

    private float x, initialX, y, startY, targetY;
    private LongGong longGong;

    private boolean gateFlashed = false;
    private boolean gongFlashed = false;

    private static final float EFFECT_DUR = 6.9F;

    public LongMenEffect(float cx, float cy, LongGong longGong) {
        this.img = ImageMaster.loadImage(GuZhenRen.assetPath("img/monsters/LongGong/LongMen.png"));

        this.gateRegion = new TextureRegion(this.img, 0, 0, 842, 828);
        this.shadowRegion = new TextureRegion(this.img, 0, 853, 842, 111);

        this.initialX = cx - this.img.getWidth() / 2.0F + 25.0F * Settings.scale;
        this.x = this.initialX;

        this.targetY = cy + 50.0F * Settings.scale;
        this.startY = this.targetY + 100.0F * Settings.scale;
        this.y = this.startY;

        this.longGong = longGong;
        this.duration = EFFECT_DUR;
        this.color = Color.WHITE.cpy();
        this.color.a = 0.0F;

        this.renderBehind = true;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        float timePassed = EFFECT_DUR - this.duration;

        if (timePassed < 0.4F) {
            if (!this.gateFlashed) {
                CardCrawlGame.sound.playA("BELL", -0.2F);
                CardCrawlGame.sound.playV("STANCE_ENTER_DIVINE", 1.5F);
                AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD));
                AbstractDungeon.effectsQueue.add(new IntenseZoomEffect(this.x + this.gateRegion.getRegionWidth() / 2.0F, this.y + this.gateRegion.getRegionHeight() / 2.0F, false));
                this.gateFlashed = true;
            }
            float progress = Math.min(1.0F, timePassed / 0.4F);
            this.color.a = Interpolation.fade.apply(0.0F, 1.0F, progress);
            this.y = Interpolation.pow3In.apply(this.startY, this.targetY, progress);
        }
        else if (timePassed < 0.9F) {
            this.color.a = 1.0F;
            this.y = this.targetY;
        }
        else if (timePassed < 4.9F) {
            this.color.a = 1.0F;
            this.y = this.targetY;

            if (!this.gongFlashed) {
                CardCrawlGame.sound.playV("MONSTER_COLLECTOR_SUMMON", 3.0F);
                CardCrawlGame.sound.playV("POWER_MANTRA", 2.0F);
                AbstractDungeon.effectsQueue.add(new SanctityEffect(this.longGong.hb.cX, this.longGong.hb.cY));
                AbstractDungeon.effectsQueue.add(new EmptyStanceEffect(this.longGong.hb.cX, this.longGong.hb.cY));

                this.longGong.setAnimAlpha(1.0F);
                this.longGong.showHealthBar();
                this.gongFlashed = true;
            }
        }
        else if (timePassed < 6.9F) {
            this.longGong.setAnimAlpha(1.0F);
            float progress = (timePassed - 4.9F) / 2.0F;
            this.color.a = Interpolation.fade.apply(1.0F, 0.0F, progress);
        }
        else {
            this.longGong.setAnimAlpha(1.0F);
            this.isDone = true;
            if (this.img != null) this.img.dispose();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!this.isDone && this.img != null) {
            sb.setColor(this.color);
            float shadowDrawY = this.targetY - 130.0F * Settings.scale;
            sb.draw(this.shadowRegion, this.x, shadowDrawY);
            float gateDrawY = this.y - 68.0F * Settings.scale;
            sb.draw(this.gateRegion, this.x, gateDrawY);
        }
    }

    @Override
    public void dispose() {
        if (this.img != null) this.img.dispose();
    }
}