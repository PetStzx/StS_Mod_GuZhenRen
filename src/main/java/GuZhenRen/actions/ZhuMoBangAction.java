package GuZhenRen.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.GiantTextEffect;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;

public class ZhuMoBangAction extends AbstractGameAction {
    private DamageInfo info;

    public ZhuMoBangAction(AbstractCreature target, DamageInfo info) {
        this.target = target;
        this.info = info;
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST && this.target != null) {
            AbstractDungeon.effectList.add(new WeightyImpactEffect(this.target.hb.cX, this.target.hb.cY));
        }

        this.tickDuration();

        if (this.isDone && this.target != null) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.BLUNT_HEAVY));

            this.target.damage(this.info);

            if (!this.target.isDeadOrEscaped() && this.target.currentHealth <= this.target.maxHealth * 0.3F) {
                AbstractDungeon.effectList.add(new GiantTextEffect(this.target.hb.cX, this.target.hb.cY));
                AbstractDungeon.actionManager.addToTop(new InstantKillAction(this.target));
            }

            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
    }
}