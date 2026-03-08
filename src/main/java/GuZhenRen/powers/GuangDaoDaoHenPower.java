package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class GuangDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("GuangDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public GuangDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        // 计算显示的百分比
        int bonus = this.amount * 20;
        this.description = powerStrings.DESCRIPTIONS[0] + bonus + powerStrings.DESCRIPTIONS[1];
    }

    // =========================================================================
    // 【核心逻辑】 增加光道牌的伤害
    // =========================================================================
    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        // 1. 判断是否为普通伤害且带有光道标签
        if (type == DamageInfo.DamageType.NORMAL && card.hasTag(GuZhenRenTags.GUANG_DAO)) {
            // 2. 每层增加 20%
            float multiplier = 1.0F + (this.amount * 0.20F);
            return damage * multiplier;
        }
        return damage;
    }
}