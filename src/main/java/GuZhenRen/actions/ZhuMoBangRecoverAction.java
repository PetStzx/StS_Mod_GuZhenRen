package GuZhenRen.actions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.DamageNumberEffect;
import com.megacrit.cardcrawl.vfx.combat.ReaperEffect;
import com.megacrit.cardcrawl.vfx.combat.HemokinesisEffect; // 【新增导包】引入血球飞行特效
import GuZhenRen.powers.XueYuanMarkPower;

public class ZhuMoBangRecoverAction extends AbstractGameAction {

    public ZhuMoBangRecoverAction() {
        this.actionType = ActionType.HEAL;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.effectList.add(new ReaperEffect());

            int totalHeal = 0;

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped() && mo.hasPower(XueYuanMarkPower.POWER_ID)) {
                    int amountToSiphon = Math.min(5, mo.currentHealth);
                    if (amountToSiphon > 0) {

                        AbstractDungeon.effectList.add(new HemokinesisEffect(mo.hb.cX, mo.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY));

                        mo.currentHealth -= amountToSiphon;
                        mo.healthBarUpdatedEvent();

                        AbstractDungeon.effectList.add(new DamageNumberEffect(mo, mo.hb.cX, mo.hb.cY, amountToSiphon));
                        totalHeal += amountToSiphon;

                        if (mo.currentHealth <= 0) {
                            mo.die();
                        }
                    }
                }
            }

            if (totalHeal > 0) {
                AbstractDungeon.effectList.add(new BorderFlashEffect(Color.RED));
                AbstractDungeon.player.heal(totalHeal);
            }
        }

        this.tickDuration();
    }
}