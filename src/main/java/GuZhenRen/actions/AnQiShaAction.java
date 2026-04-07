package GuZhenRen.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.ViceCrushEffect;

public class AnQiShaAction extends AbstractGameAction {
    private DamageInfo info;
    private boolean shouldMultiply;

    public AnQiShaAction(AbstractCreature target, DamageInfo info, boolean shouldMultiply) {
        this.info = info;
        this.target = target;
        this.shouldMultiply = shouldMultiply;
        this.actionType = ActionType.DAMAGE;
        this.duration = 0.1F;
    }

    @Override
    public void update() {
        if (this.duration == 0.1F && this.target != null) {
            if (this.shouldMultiply) {
                this.info.output *= 10;
                AbstractDungeon.effectList.add(new ViceCrushEffect(this.target.hb.cX, this.target.hb.cY));
            }

            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.SLASH_HEAVY));
            this.target.damage(this.info);

            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
    }
}