package GuZhenRen.patches;

import GuZhenRen.GuZhenRen;
import GuZhenRen.effects.LongMenEffect;
import GuZhenRen.monsters.LongGong;
import GuZhenRen.monsters.QiQiang;
import GuZhenRen.util.FinalBossChoiceManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;

public class LongGongBossPatch {

    public static boolean hasCinematicPlayed = false;

    public static class CinematicWaitAction extends AbstractGameAction {
        private float timer;
        public CinematicWaitAction(float duration) {
            this.timer = duration;
            this.duration = duration;
        }
        @Override
        public void update() {
            this.timer -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
            if (this.timer <= 0.0F) {
                this.isDone = true;
            }
        }
    }

    @SpirePatch(clz = MonsterRoomBoss.class, method = "onPlayerEntry")
    public static class ReplaceBossPatch {
        @SpirePrefixPatch
        public static void Prefix(MonsterRoomBoss __instance) {
            hasCinematicPlayed = false;
            if (AbstractDungeon.id.equals(TheEnding.ID)) {
                if (FinalBossChoiceManager.shouldUseLongGong()) {
                    AbstractDungeon.bossKey = GuZhenRen.ENCOUNTER_LONG_GONG;
                }
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
    public static class EntranceCinematicPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance) {
            if (!hasCinematicPlayed && AbstractDungeon.actionManager.turn == 1 &&
                    GuZhenRen.ENCOUNTER_LONG_GONG.equals(AbstractDungeon.bossKey)) {

                hasCinematicPlayed = true;
                AbstractDungeon.getCurrRoom().cannotLose = false;

                LongGong longGong = new LongGong(0.0F, 0.0F);
                longGong.lockAlpha = true;
                longGong.setAnimAlpha(0.0F);
                longGong.hideHealthBar();
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(longGong, false));

                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        longGong.intent = AbstractMonster.Intent.NONE;
                        longGong.createIntent();
                        this.isDone = true;
                    }
                });

                AbstractMonster heart = null;
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.id.equals(CorruptHeart.ID)) {
                        heart = m;
                        break;
                    }
                }

                if (heart != null) {
                    AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(0.5F));
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(new WeightyImpactEffect(heart.hb.cX, heart.hb.cY), 1.0F));
                    AbstractDungeon.actionManager.addToBottom(new InstantKillAction(heart));
                    AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(2.0F));
                }

                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractDungeon.scene.fadeOutAmbiance();
                        CardCrawlGame.music.silenceBGMInstantly();
                        CardCrawlGame.music.silenceTempBgmInstantly();
                        CardCrawlGame.music.playTempBgmInstantly("BOSS_BEYOND", true);

                        AbstractDungeon.effectList.add(new LongMenEffect(longGong.hb.cX, longGong.drawY, longGong));
                        this.isDone = true;
                    }
                });

                AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(3.1F));
                AbstractDungeon.actionManager.addToBottom(new TalkAction(longGong, LongGong.DIALOG[0], 0.5F, 2.0F));
                AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(1.5F));
                AbstractDungeon.actionManager.addToBottom(new TalkAction(longGong, LongGong.DIALOG[1], 1.0F, 3.0F));
                AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(1.5F));

                QiQiang qiQiang = new QiQiang(-400.0F, 0.0F);
                qiQiang.lockAlpha = true;
                qiQiang.setAnimAlpha(0.0F);
                qiQiang.hideHealthBar();

                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(qiQiang, false));

                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    private float duration = 1.0F;
                    private boolean isFirstFrame = true;
                    private float targetX = 0.0F;
                    private float startX = 0.0F;

                    @Override
                    public void update() {
                        if (this.isFirstFrame) {
                            CardCrawlGame.sound.playA("ATTACK_MAGIC_SLOW_1", -0.2F);
                            CardCrawlGame.sound.playA("EXHAUST", -0.1F);

                            this.targetX = qiQiang.drawX;
                            this.startX = this.targetX + 150.0F * Settings.scale;
                            qiQiang.drawX = this.startX;

                            qiQiang.showHealthBar();
                            this.isFirstFrame = false;
                        }

                        this.duration -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();

                        if (this.duration <= 0.0F) {
                            qiQiang.setAnimAlpha(1.0F);
                            qiQiang.drawX = this.targetX;
                            this.isDone = true;
                        } else {
                            float progress = 1.0F - this.duration / 1.0F;
                            float a = com.badlogic.gdx.math.Interpolation.fade.apply(0.0F, 1.0F, progress);
                            qiQiang.setAnimAlpha(a);
                            qiQiang.drawX = com.badlogic.gdx.math.Interpolation.pow2Out.apply(this.startX, this.targetX, progress);
                        }
                    }
                });

                AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(1.5F));

                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        longGong.lockAlpha = false;
                        longGong.usePreBattleAction();
                        longGong.rollMove();
                        longGong.createIntent();

                        qiQiang.lockAlpha = false;
                        qiQiang.usePreBattleAction();
                        qiQiang.rollMove();
                        qiQiang.createIntent();

                        AbstractDungeon.getCurrRoom().cannotLose = true;
                        this.isDone = true;
                    }
                });
            }
        }
    }
}