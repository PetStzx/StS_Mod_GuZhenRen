package GuZhenRen.monsters;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.QiHuPower;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class QiQiang extends AbstractMonster {
    public static final String ID = GuZhenRen.makeID("QiQiang");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final int MAX_HP = 250;
    private static final byte INTENT_HEAL = 1;
    private static final int HEAL_AMT = 30;

    public boolean lockAlpha = false;
    public float lockedAlpha = 1.0F;

    public QiQiang(float x, float y) {
        super(NAME, ID, MAX_HP, 0.0F, -10.0F, 200.0F, 500.0F, null, x, y);

        this.loadAnimation(
                GuZhenRen.assetPath("img/monsters/QiQiang/QiQiang.atlas"),
                GuZhenRen.assetPath("img/monsters/QiQiang/QiQiang.json"),
                3.3F
        );

        com.esotericsoftware.spine.AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * com.badlogic.gdx.math.MathUtils.random());
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(this, this, new QiHuPower(this))
        );
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
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && this.currentHealth > 0) {
            this.skeleton.setToSetupPose();
            this.state.setAnimation(0, "Idle", true);
        }
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case INTENT_HEAL:
                AbstractDungeon.actionManager.addToBottom(
                        new HealAction(this, this, HEAL_AMT)
                );
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        this.setMove(INTENT_HEAL, Intent.BUFF);
    }
}