package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

public class ShaDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("ShaDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    // 每层设定的基础伤害值为 3
    private static final int DAMAGE_PER_STACK = 3;

    public ShaDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        int totalDamage = this.amount * DAMAGE_PER_STACK;
        this.description = powerStrings.DESCRIPTIONS[0] + totalDamage + powerStrings.DESCRIPTIONS[1];
    }

    // =========================================================================
    // 【核心逻辑】 回合结束时触发群体顺劈伤害
    // =========================================================================
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && this.amount > 0) {
            this.flash();

            // 1. 播放顺劈斩的重击音效
            this.addToBot(new SFXAction("ATTACK_HEAVY"));

            // 2. 播放顺劈斩的视觉特效
            this.addToBot(new VFXAction(this.owner, new CleaveEffect(), 0.1F));

            // 3. 结算群体伤害
            int totalDamage = this.amount * DAMAGE_PER_STACK;

            // 构造群体伤害数组
            int[] damageArray = new int[AbstractDungeon.getCurrRoom().monsters.monsters.size()];
            for (int i = 0; i < damageArray.length; ++i) {
                damageArray[i] = totalDamage;
            }

            // 使用 THORNS 伤害类型，这样它属于被动伤害，不会触发“打出攻击牌”的相关效果
            this.addToBot(new DamageAllEnemiesAction(
                    this.owner,
                    damageArray,
                    DamageInfo.DamageType.THORNS,
                    AbstractGameAction.AttackEffect.NONE
            ));
        }
    }
}