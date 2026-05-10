package GuZhenRen.vfx;

import basemod.BaseMod;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.stance.DivinityStanceChangeParticle;
import GuZhenRen.util.BattleStateManager;

public class TianYiEffect extends AbstractGameEffect implements PostUpdateSubscriber {
    private float particleTimer = 0.0F;
    private float auraTimer = 0.0F;
    private long sfxId = -1L;

    private boolean isRegistered = true;

    public TianYiEffect() {
        this.duration = 5.0F;

        CardCrawlGame.sound.play("STANCE_ENTER_DIVINITY");
        this.sfxId = CardCrawlGame.sound.playAndLoop("STANCE_LOOP_DIVINITY");

        BaseMod.subscribe(this);

        BattleStateManager.onPostBattle(() -> {
            this.stopSound();
        });

        AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.WHITE, true));

        for(int i = 0; i < 20; ++i) {
            AbstractDungeon.effectsQueue.add(new DivinityStanceChangeParticle(
                    Color.WHITE,
                    AbstractDungeon.player.hb.cX,
                    AbstractDungeon.player.hb.cY
            ));
        }
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();

        if (this.duration > 0.0F) {
            this.particleTimer -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer < 0.0F) {
                this.particleTimer = 0.2F;
                AbstractDungeon.effectsQueue.add(new TianYiParticleEffect());
            }

            this.auraTimer -= Gdx.graphics.getDeltaTime();
            if (this.auraTimer < 0.0F) {
                this.auraTimer = MathUtils.random(0.45F, 0.55F);
                AbstractDungeon.effectsQueue.add(new TianYiAuraEffect());
            }
        } else {
            this.isDone = true;
            this.stopSound();
        }
    }

    @Override
    public void receivePostUpdate() {
        if (CardCrawlGame.mode == CardCrawlGame.GameMode.CHAR_SELECT) {
            this.stopSound();
        }
    }

    // 封装的清理方法
    private void stopSound() {
        if (this.sfxId != -1L) {
            CardCrawlGame.sound.fadeOut("STANCE_LOOP_DIVINITY", this.sfxId);
            this.sfxId = -1L;
        }

        if (this.isRegistered) {
            BaseMod.unsubscribeLater(this);
            this.isRegistered = false;
        }
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch sb) {
    }

    @Override
    public void dispose() {
        this.stopSound();
    }
}