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
        this.shouldMultiply = shouldMultiply; // 是否满足条件
        this.actionType = ActionType.DAMAGE;
        this.duration = 0.1F; // 动作执行时间
    }

    @Override
    public void update() {
        if (this.duration == 0.1F && this.target != null) {
            int dmg = this.info.output; // 面板最终算好的伤害
            int block = this.target.currentBlock; // 怪物当前的护甲值

            // 如果满足条件，且伤害大于怪物的护甲，执行十倍暴击逻辑！
            if (this.shouldMultiply && dmg > block) {
                int unblocked = dmg - block; // 计算未被格挡的真实伤害
                this.info.output = block + (unblocked * 10); // 将原伤害替换为：破甲伤害 + 10倍真实伤害

                // “重击”特效
                AbstractDungeon.effectList.add(new ViceCrushEffect(this.target.hb.cX, this.target.hb.cY));
            }

            // 播放普通斩击特效并造成最终伤害
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.SLASH_HEAVY));
            this.target.damage(this.info);

            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
    }
}