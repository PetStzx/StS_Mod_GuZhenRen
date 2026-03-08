package GuZhenRen.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
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
        // 1. 播放特效
        if (this.duration == Settings.ACTION_DUR_FAST && this.target != null) {
            AbstractDungeon.effectList.add(new WeightyImpactEffect(this.target.hb.cX, this.target.hb.cY));
        }

        this.tickDuration();

        // 2. 执行伤害和斩杀判定
        if (this.isDone && this.target != null) {
            // 记录扣血前的生命值
            int hpBefore = this.target.currentHealth;
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.BLUNT_HEAVY));

            // 造成伤害
            this.target.damage(this.info);

            // 计算实际失去的生命值并获得等量格挡 (斩杀的掉血不计入此列)
            int actualLostHP = hpBefore - this.target.currentHealth;
            if (actualLostHP > 0) {
                AbstractDungeon.actionManager.addToTop(new GainBlockAction(this.info.owner, this.info.owner, actualLostHP));
            }

            // 3. 斩杀判定：存活且生命值低于上限的 20%
            if (!this.target.isDeadOrEscaped() && this.target.currentHealth <= this.target.maxHealth * 0.2F) {

                // 调用原版“审判”使用的文字特效
                AbstractDungeon.effectList.add(new GiantTextEffect(this.target.hb.cX, this.target.hb.cY));

                // 斩杀 Action
                AbstractDungeon.actionManager.addToTop(new InstantKillAction(this.target));
            }

            // 如果怪物全死了，清空后续队列防止报错
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
    }
}