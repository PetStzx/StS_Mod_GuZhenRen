package GuZhenRen.monsters;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.YiLuan;
import GuZhenRen.patches.LongGongBossPatch.CinematicWaitAction;
import GuZhenRen.powers.DiQiKuiSanPower;
import GuZhenRen.powers.JiuLongWenHuShenPower;
import GuZhenRen.powers.LongYuShangBinPower;
import GuZhenRen.powers.RenQiKuiSanPower;
import GuZhenRen.powers.SanQiGuiLaiPower;
import GuZhenRen.powers.TianQiKuiSanPower;
import GuZhenRen.powers.ZhuanZhuPower;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Bone;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.ClearCardQueueAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.AwakenedEyeParticle;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;
import com.megacrit.cardcrawl.vfx.combat.WhirlwindEffect;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;

public class LongGong extends AbstractMonster {
    public static final String ID = GuZhenRen.makeID("LongGong");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int MAX_HP = 800;

    public static final byte INTENT_LUAN_LONG_QUAN = 1;
    public static final byte INTENT_QI_HU_SHAN = 2;
    public static final byte INTENT_LONG_ZHAO_JI = 3;
    public static final byte INTENT_SUMMON = 4;
    public static final byte INTENT_REN_QI = 10;
    public static final byte INTENT_DI_QI = 11;
    public static final byte INTENT_TIAN_QI = 12;

    public static final byte INTENT_QI_GAI_SHAN_HE = 5;
    public static final byte INTENT_HUI_XUAN_LONG_YA = 6;
    public static final byte INTENT_YI_QI_DA_SHOU = 7;
    public static final byte INTENT_HAN_SHI_LONG_CHUI = 8;

    public static final byte INTENT_SAN_QI_GUI_LAI = 9;

    private int turnCount = 1;
    private int phase = 1;
    public boolean summonedYouLong = false;

    public boolean isSecondPhase = false;
    private boolean phase2PowersApplied = false;

    public boolean lockAlpha = false;
    public float lockedAlpha = 1.0F;

    private Bone qiEye;
    private float fireTimer = 0.0F;

    public LongGong(float x, float y) {
        super(NAME, ID, MAX_HP, 0.0F, -10.0F, 400.0F, 500.0F, null, x, y);

        this.damage.add(new DamageInfo(this, 4));  // 0: 乱龙拳
        this.damage.add(new DamageInfo(this, 32)); // 1: 气呼山
        this.damage.add(new DamageInfo(this, 8)); // 2: 龙爪击
        this.damage.add(new DamageInfo(this, 10)); // 3: 气盖山河
        this.damage.add(new DamageInfo(this, 1));  // 4: 回旋龙牙
        this.damage.add(new DamageInfo(this, 40)); // 5: 一气大手爆
        this.damage.add(new DamageInfo(this, 5)); // 6: 撼世龙锤

        this.loadAnimation(
                GuZhenRen.assetPath("img/monsters/LongGong/LongGong.atlas"),
                GuZhenRen.assetPath("img/monsters/LongGong/LongGong.json"),
                3.3F
        );

        this.stateData.setMix("Idle_1", "Idle_2", 1.5F);
        this.stateData.setMix("Hit_1", "Idle_1", 0.2F);
        this.stateData.setMix("Hit_2", "Idle_2", 0.2F);
        this.stateData.setMix("Attack_1", "Idle_1", 0.2F);
        this.stateData.setMix("Attack_2", "Idle_2", 0.2F);

        com.esotericsoftware.spine.AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle_1", true);
        e.setTime(e.getEndTime() * com.badlogic.gdx.math.MathUtils.random());

        this.qiEye = this.skeleton.findBone("Qi_eye");

        this.dialogX = -100.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.getCurrRoom().cannotLose = true;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new LongYuShangBinPower(this, 40)));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new JiuLongWenHuShenPower(this, 9)));
    }

    public void setAnimAlpha(float alpha) {
        this.lockedAlpha = alpha;
    }

    @Override
    public void update() {
        super.update();

        if (this.lockAlpha) {
            this.intent = Intent.NONE;
            this.tint.color.a = this.lockedAlpha;
            if (this.skeleton != null) {
                this.skeleton.setColor(new Color(1.0F, 1.0F, 1.0F, this.lockedAlpha));
            }
        }

        if (!this.isDying && this.isSecondPhase && !this.halfDead && this.qiEye != null) {
            this.fireTimer -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
            if (this.fireTimer < 0.0F) {
                this.fireTimer = 0.1F;
                AbstractDungeon.effectList.add(new AwakenedEyeParticle(
                        this.skeleton.getX() + this.qiEye.getWorldX(),
                        this.skeleton.getY() + this.qiEye.getWorldY()
                ));
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.lockAlpha && this.lockedAlpha <= 0.0F) {
            return;
        }
        super.render(sb);
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        if (this.lockAlpha) {
            return;
        }
        super.renderTip(sb);
    }

    @Override
    public void damage(DamageInfo info) {
        int previousHealth = this.currentHealth;
        super.damage(info);

        if (this.hasPower(ZhuanZhuPower.POWER_ID) && this.currentHealth < previousHealth) {
            AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(this, this, ZhuanZhuPower.POWER_ID));

            this.setMove((byte)-2, Intent.STUN);
            this.createIntent();
            AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(this, TextAboveCreatureAction.TextType.STUNNED));
        }

        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && this.currentHealth > 0) {
            this.skeleton.setToSetupPose();
            if (this.isSecondPhase) {
                this.state.setAnimation(0, "Hit_2", false);
                this.state.addAnimation(0, "Idle_2", true, 0.0F);
            } else {
                this.state.setAnimation(0, "Hit_1", false);
                this.state.addAnimation(0, "Idle_1", true, 0.0F);
            }
        }

        if (this.currentHealth <= 0 && !this.halfDead && !this.isSecondPhase) {
            if (AbstractDungeon.getCurrRoom().cannotLose) {
                this.halfDead = true;
            }

            for (AbstractPower p : this.powers) { p.onDeath(); }
            for (AbstractRelic r : AbstractDungeon.player.relics) { r.onMonsterDeath(this); }

            this.addToTop(new ClearCardQueueAction());
            this.powers.removeIf(p -> p.type == AbstractPower.PowerType.DEBUFF || p.ID.equals("Shackled"));

            this.setMove(MOVES[6], INTENT_SAN_QI_GUI_LAI, Intent.UNKNOWN);
            this.createIntent();

            AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[2], 1.0F, 1.0F));
            AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(1.0F));
            AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[3], 1.0F, 1.0F));
            AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(1.0F));
            AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[4], 1.0F, 2.0F));

            AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, MOVES[6], INTENT_SAN_QI_GUI_LAI, Intent.UNKNOWN));

            this.applyPowers();
            this.isSecondPhase = true;
        }
    }

    @Override
    public void changeState(String stateName) {
        if (stateName.equals("ATTACK")) {
            if (this.isSecondPhase) {
                this.state.setAnimation(0, "Attack_2", false);
                this.state.addAnimation(0, "Idle_2", true, 0.0F);
            } else {
                this.state.setAnimation(0, "Attack_1", false);
                this.state.addAnimation(0, "Idle_1", true, 0.0F);
            }
        } else if (stateName.equals("REBIRTH")) {
            this.maxHealth = 800;
            this.halfDead = false;

            if (this.skeleton != null) {
                this.skeleton.setSlotsToSetupPose();
            }

            this.state.setAnimation(0, "Idle_2", true);

            AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, this.maxHealth));
            AbstractDungeon.actionManager.addToBottom(new CanLoseAction());

            AbstractPower longYu = this.getPower(LongYuShangBinPower.POWER_ID);
            int currentLongYu = (longYu != null) ? longYu.amount : 0;
            if (currentLongYu < 200) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new LongYuShangBinPower(this, 200 - currentLongYu), 200 - currentLongYu));
            }

            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new JiuLongWenHuShenPower(this, 9), 9));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new SanQiGuiLaiPower(this)));
        }
    }

    @Override
    public void takeTurn() {
        if (this.hasPower(ZhuanZhuPower.POWER_ID)) {
            AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(this, this, ZhuanZhuPower.POWER_ID));
        }

        switch (this.nextMove) {
            case INTENT_LUAN_LONG_QUAN:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < 3; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new YiLuan(), 1, true, true));
                }
                break;
            case INTENT_QI_HU_SHAN:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(AbstractDungeon.player.hb.cX, Settings.HEIGHT, Color.WHITE.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC), 0.3F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new BorderFlashEffect(Color.WHITE.cpy(), true)));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
            case INTENT_LONG_ZHAO_JI:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 2, true), 2));
                break;
            case INTENT_SUMMON:
                AbstractMonster targetWallTemp = null;
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m.id.equals(QiQiang.ID) && !m.isDeadOrEscaped()) {
                        targetWallTemp = m;
                        break;
                    }
                }
                final AbstractMonster targetWall = targetWallTemp;
                if (targetWall != null) {
                    AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                        boolean firstFrame = true;
                        YouLongQiQiang newWall;
                        @Override
                        public void update() {
                            if (firstFrame) {
                                this.duration = 1.0F;
                                firstFrame = false;
                                float oX = (targetWall.drawX - (Settings.WIDTH * 0.75F)) / Settings.scale;
                                float oY = (targetWall.drawY - AbstractDungeon.floorY) / Settings.scale;
                                newWall = new YouLongQiQiang(oX, oY);

                                newWall.lockAlpha = true;
                                newWall.setAnimAlpha(0.0F);

                                if (targetWall instanceof QiQiang) {
                                    ((QiQiang)targetWall).lockAlpha = true;
                                }
                                newWall.init();
                                newWall.currentHealth = Math.min(newWall.maxHealth, targetWall.currentHealth + 100);
                                float barWidth = newWall.hb.width * ((float)newWall.currentHealth / (float)newWall.maxHealth);
                                basemod.ReflectionHacks.setPrivate(newWall, com.megacrit.cardcrawl.core.AbstractCreature.class, "healthBarWidth", barWidth);
                                basemod.ReflectionHacks.setPrivate(newWall, com.megacrit.cardcrawl.core.AbstractCreature.class, "targetHealthBarWidth", barWidth);
                                newWall.applyPowers();
                                newWall.usePreBattleAction();
                                int index = AbstractDungeon.getMonsters().monsters.indexOf(targetWall);
                                if (index != -1) {
                                    AbstractDungeon.getMonsters().monsters.add(index, newWall);
                                } else {
                                    AbstractDungeon.getMonsters().add(newWall);
                                }
                                newWall.showHealthBar();
                                targetWall.hideHealthBar();
                            }

                            this.tickDuration();
                            float progress = 1.0F - (this.duration / 1.0F);

                            if (targetWall instanceof QiQiang) {
                                ((QiQiang)targetWall).setAnimAlpha(1.0F - progress);
                            }
                            newWall.setAnimAlpha(progress);

                            if (this.isDone) {
                                newWall.lockAlpha = false;
                                newWall.setAnimAlpha(1.0F);
                                targetWall.isDead = true;
                                targetWall.isDying = true;
                                AbstractDungeon.getMonsters().monsters.remove(targetWall);
                            }
                        }
                    });
                    this.summonedYouLong = true;
                } else {
                    AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new LongQi(-400.0F, 0.0F), false));
                    this.summonedYouLong = false;
                }
                break;
            case INTENT_REN_QI:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, Color.CYAN.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC), 0.0F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new RenQiKuiSanPower(AbstractDungeon.player)));
                break;
            case INTENT_DI_QI:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, Color.BROWN.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC), 0.0F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DiQiKuiSanPower(AbstractDungeon.player)));
                break;
            case INTENT_TIAN_QI:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, Color.LIGHT_GRAY.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC), 0.0F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new TianQiKuiSanPower(AbstractDungeon.player)));
                break;

            case INTENT_SAN_QI_GUI_LAI:
                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[5], 1.0F, 1.0F));
                AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(1.0F));

                AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[6], 2.0F, 3.0F));

                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        CardCrawlGame.sound.playAV(GuZhenRen.makeID("LongYin1"), 0.0F, 0.6F);
                        this.isDone = true;
                    }
                });
                AbstractDungeon.actionManager.addToBottom(new SFXAction("AWAKENED_AWAKEN"));

                AbstractDungeon.actionManager.addToBottom(new VFXAction(new BorderFlashEffect(Color.GOLD.cpy())));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new IntenseZoomEffect(this.hb.cX, this.hb.cY, false)));

                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        LongGong.this.hb_h -= 60.0F * Settings.scale;
                        LongGong.this.hb.height -= 60.0F * Settings.scale;
                        this.isDone = true;
                    }
                });

                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(this.hb.cX, this.hb.cY, Color.BROWN.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC), 0.0F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(this.hb.cX, this.hb.cY, Color.CYAN.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC), 0.0F));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(this.hb.cX, this.hb.cY, Color.LIGHT_GRAY.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC), 0.5F));

                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "REBIRTH"));
                break;

            case INTENT_QI_GAI_SHAN_HE:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        CardCrawlGame.sound.playAV(GuZhenRen.makeID("LongYin2"), 0.0F, 0.6F);
                        this.isDone = true;
                    }
                });
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new BorderFlashEffect(Color.WHITE.cpy(), true)));

                float topY = Settings.HEIGHT + 100.0F * Settings.scale;
                float leftX = -100.0F * Settings.scale;
                float centerX = Settings.WIDTH / 2.0F;
                float rightX = Settings.WIDTH + 100.0F * Settings.scale;

                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(leftX, topY, Color.WHITE.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC)));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(centerX, topY, Color.WHITE.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC)));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(rightX, topY, Color.WHITE.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC)));

                AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(0.5F));

                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 10, true), 10));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 10, true), 10));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, 10, true), 10));
                break;

            case INTENT_HUI_XUAN_LONG_YA:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        CardCrawlGame.sound.playAV(GuZhenRen.makeID("LongYin1"), 0.3F, 0.6F);
                        this.isDone = true;
                    }
                });

                AbstractDungeon.actionManager.addToBottom(new VFXAction(new WhirlwindEffect(new Color(0.9F, 0.9F, 1.0F, 1.0F), false)));
                AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(0.5F));
                for (int i = 0; i < 2; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(4), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                }

                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new JiuLongWenHuShenPower(this, 4), 4));
                break;

            case INTENT_YI_QI_DA_SHOU:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        CardCrawlGame.sound.playAV(GuZhenRen.makeID("LongYin2"), 0.2F, 0.6F);
                        this.isDone = true;
                    }
                });

                AbstractDungeon.actionManager.addToBottom(new VFXAction(new BorderFlashEffect(Color.WHITE.cpy(), true), 0.1F));

                float offset = 800.0F * Settings.scale;
                float pX = AbstractDungeon.player.hb.cX;
                float pY = AbstractDungeon.player.hb.cY;
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(pX - offset, pY - offset, Color.WHITE.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC)));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(pX + offset, pY - offset, Color.WHITE.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC)));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(pX - offset, pY + offset, Color.WHITE.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC)));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new ShockWaveEffect(pX + offset, pY + offset, Color.WHITE.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC)));
                AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(1.0F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(5), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;

            case INTENT_HAN_SHI_LONG_CHUI:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        CardCrawlGame.sound.playAV(GuZhenRen.makeID("LongYin1"), 0.1F, 0.6F);
                        this.isDone = true;
                    }
                });

                AbstractDungeon.actionManager.addToBottom(new VFXAction(new WeightyImpactEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY)));
                AbstractDungeon.actionManager.addToBottom(new CinematicWaitAction(0.8F));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(6), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
        }

        if (!this.isSecondPhase) {
            this.turnCount++;
            if (this.phase == 1 && this.turnCount > 3) {
                this.phase = this.summonedYouLong ? 2 : 3;
            } else if (this.phase == 2 && this.turnCount > 6) {
                this.phase = 3;
            }
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        if (this.isSecondPhase) {
            if (this.lastMove(INTENT_SAN_QI_GUI_LAI) || this.lastMove(INTENT_YI_QI_DA_SHOU)) {
                this.setMove(MOVES[7], INTENT_QI_GAI_SHAN_HE, Intent.ATTACK_DEBUFF, this.damage.get(3).base);
            } else if (this.lastMove(INTENT_QI_GAI_SHAN_HE)) {
                this.setMove(MOVES[8], INTENT_HUI_XUAN_LONG_YA, Intent.ATTACK_BUFF, this.damage.get(4).base, 2, true);
            } else if (this.lastMove(INTENT_HUI_XUAN_LONG_YA)) {
                this.setMove(MOVES[10], INTENT_HAN_SHI_LONG_CHUI, Intent.ATTACK, this.damage.get(6).base);
            } else {
                this.setMove(MOVES[9], INTENT_YI_QI_DA_SHOU, Intent.ATTACK, this.damage.get(5).base);
            }
            return;
        }

        if (this.phase == 2) {
            if (this.lastMove((byte)-2)) {
                this.phase = 3;
            }
        }

        if (this.phase == 1) {
            if (this.turnCount == 1) {
                this.setMove(MOVES[0], INTENT_LUAN_LONG_QUAN, Intent.ATTACK_DEBUFF, this.damage.get(0).base, 3, true);
            } else if (this.turnCount == 2) {
                this.setMove(MOVES[1], INTENT_QI_HU_SHAN, Intent.ATTACK, this.damage.get(1).base);
            } else if (this.turnCount == 3) {
                this.setMove(INTENT_SUMMON, Intent.UNKNOWN);
            }
        }
        else if (this.phase == 2) {
            if (this.turnCount == 4) {
                this.setMove(MOVES[3], INTENT_REN_QI, Intent.STRONG_DEBUFF);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ZhuanZhuPower(this)));
            } else if (this.turnCount == 5) {
                this.setMove(MOVES[4], INTENT_DI_QI, Intent.STRONG_DEBUFF);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ZhuanZhuPower(this)));
            } else if (this.turnCount == 6) {
                this.setMove(MOVES[5], INTENT_TIAN_QI, Intent.STRONG_DEBUFF);
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ZhuanZhuPower(this)));
            }
        }

        if (this.phase == 3) {
            if (this.lastMove(INTENT_LONG_ZHAO_JI)) {
                this.setMove(MOVES[0], INTENT_LUAN_LONG_QUAN, Intent.ATTACK_DEBUFF, this.damage.get(0).base, 3, true);
            } else if (this.lastMove(INTENT_LUAN_LONG_QUAN)) {
                this.setMove(MOVES[1], INTENT_QI_HU_SHAN, Intent.ATTACK, this.damage.get(1).base);
            } else {
                this.setMove(MOVES[2], INTENT_LONG_ZHAO_JI, Intent.ATTACK_DEBUFF, this.damage.get(2).base);
            }
        }
    }

    @Override
    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            this.useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            ++this.deathTimer;
            super.die();
            this.onBossVictoryLogic();

            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDead && !m.isDying && m != this) {
                    AbstractDungeon.actionManager.addToTop(new HideHealthBarAction(m));
                    AbstractDungeon.actionManager.addToTop(new SuicideAction(m));
                    AbstractDungeon.actionManager.addToTop(new VFXAction(new ExplosionSmallEffect(m.hb.cX, m.hb.cY), 0.1F));
                }
            }
        }
    }
}