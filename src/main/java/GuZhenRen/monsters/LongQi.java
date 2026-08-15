package GuZhenRen.monsters;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.JiuLongWenHuShenPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateJumpAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

import java.util.ArrayList;

public class LongQi extends AbstractMonster {
    public static final String ID = GuZhenRen.makeID("LongQi");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int MAX_HP = 125;

    private static final byte INTENT_QI_BAO = 1;
    private static final byte INTENT_LONG_QI = 2;

    private static final int QI_BAO_DMG = 6;
    private static final int QI_BAO_HITS = 2;
    private static final int DEBUFF_AMT = 1;
    private static final int STR_AMT = 2;
    private static final int JIU_LONG_WEN_AMT = 4;

    private boolean isFadingIn = true;
    private float fadeTimer = 1.0F; // 淡入时间1s

    public LongQi(float x, float y) {
        super(NAME, ID, MAX_HP, 0.0F, 0.0F, 350.0F, 250.0F, null, x, y + 200.0F);

        this.damage.add(new DamageInfo(this, QI_BAO_DMG));

        this.loadAnimation(
                GuZhenRen.assetPath("img/monsters/LongQi/LongQi.atlas"),
                GuZhenRen.assetPath("img/monsters/LongQi/LongQi.json"),
                4.5F
        );

        com.esotericsoftware.spine.AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * com.badlogic.gdx.math.MathUtils.random());

        this.tint.color.a = 0.0F;
    }

    // 淡入入场
    @Override
    public void update() {
        super.update();
        if (this.isFadingIn) {
            this.fadeTimer -= Gdx.graphics.getDeltaTime();
            if (this.fadeTimer <= 0.0F) {
                this.fadeTimer = 0.0F;
                this.isFadingIn = false;
            }
            this.tint.color.a = 1.0F - this.fadeTimer;
            if (this.skeleton != null) {
                this.skeleton.setColor(new Color(1.0F, 1.0F, 1.0F, this.tint.color.a));
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.isFadingIn && this.tint.color.a <= 0.0F) {
            return;
        }
        super.render(sb);
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && this.currentHealth > 0) {
            if (this.state != null) {
                this.state.setAnimation(0, "Hit", false);
                this.state.addAnimation(0, "Idle", true, 0.0F);
            }
        }
    }

    @Override
    public void changeState(String stateName) {
        if (stateName.equals("ATTACK")) {
            this.state.setAnimation(0, "Attack", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case INTENT_QI_BAO:
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new AnimateJumpAction(this));
                AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Color.WHITE.cpy(), ShockWaveEffect.ShockWaveType.CHAOTIC), 0.5F));

                for (int i = 0; i < QI_BAO_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(
                            new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY)
                    );
                }
                break;

            case INTENT_LONG_QI:
                AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(this, this, new StrengthPower(this, STR_AMT), STR_AMT)
                );

                AbstractMonster target = null;

                if (AbstractDungeon.getMonsters() != null) {
                    for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                        if (!m.isDeadOrEscaped() && m.id.equals(LongGong.ID)) {
                            target = m;
                            break;
                        }
                    }

                    if (target == null) {
                        ArrayList<AbstractMonster> validTargets = new ArrayList<>();
                        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                            if (!m.isDeadOrEscaped() && m != this) {
                                validTargets.add(m);
                            }
                        }
                        if (!validTargets.isEmpty()) {
                            target = validTargets.get(AbstractDungeon.aiRng.random(validTargets.size() - 1));
                        }
                    }
                }

                if (target == null) {
                    target = this;
                }

                AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(target, this, new JiuLongWenHuShenPower(target, JIU_LONG_WEN_AMT), JIU_LONG_WEN_AMT)
                );
                break;
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        if (this.lastMove(INTENT_QI_BAO)) {
            this.setMove(MOVES[1], INTENT_LONG_QI, Intent.BUFF);
        } else {
            this.setMove(MOVES[0], INTENT_QI_BAO, Intent.ATTACK, this.damage.get(0).base, QI_BAO_HITS, true);
        }
    }
}