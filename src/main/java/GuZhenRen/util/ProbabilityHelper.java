package GuZhenRen.util;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.ZhuanYunPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class ProbabilityHelper {

    // 加入 AbstractCard 参数
    public static float getModifiedChance(AbstractCard card, float baseChance) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return baseChance;

        float additiveMod = 0.0f;
        float baseMultiplier = 1.0f;
        float finalMultiplier = 1.0f;
        float absoluteOverride = -1.0f;

        for (AbstractPower power : p.powers) {
            if (power instanceof IProbabilityModifier) {
                IProbabilityModifier mod = (IProbabilityModifier) power;
                // 将卡牌传给能力，让能力自己判断是否要修改
                baseMultiplier *= mod.getBaseProbabilityMultiplier(card);
                additiveMod += mod.getAdditiveProbability(card);
                finalMultiplier *= mod.getFinalProbabilityMultiplier(card);

                float override = mod.getAbsoluteProbabilityOverride(card);
                if (override >= 0) absoluteOverride = override;
            }
        }

        for (AbstractRelic relic : p.relics) {
            if (relic instanceof IProbabilityModifier) {
                IProbabilityModifier mod = (IProbabilityModifier) relic;
                baseMultiplier *= mod.getBaseProbabilityMultiplier(card);
                additiveMod += mod.getAdditiveProbability(card);
                finalMultiplier *= mod.getFinalProbabilityMultiplier(card);

                float override = mod.getAbsoluteProbabilityOverride(card);
                if (override >= 0) absoluteOverride = override;
            }
        }

        float chance = (baseChance * baseMultiplier) + additiveMod;
        chance *= finalMultiplier;

        if (absoluteOverride >= 0) chance = absoluteOverride;
        if (chance > 1.0f) chance = 1.0f;
        if (chance < 0.0f) chance = 0.0f;

        return chance;
    }

    // 同理，加入参数
    public static String getDynamicColorString(AbstractCard card, float baseChance) {
        float currentChance = getModifiedChance(card, baseChance);

        int basePct = Math.round(baseChance * 100);
        int currentPct = Math.round(currentChance * 100);

        if (currentPct > basePct) {
            return "[#7fff00]" + currentPct + "%[]";
        } else if (currentPct < basePct) {
            return "[#ff6563]" + currentPct + "%[]";
        }

        return currentPct + "%";
    }

    public static boolean rollProbability(AbstractCard card, float baseChance) {
        float realChance = getModifiedChance(card, baseChance);
        boolean success = AbstractDungeon.cardRandomRng.randomBoolean(realChance);

        // 如果判定失败，寻找玩家身上的“转运”能力并触发
        if (!success && AbstractDungeon.player != null) {
            String zhuanYunId = GuZhenRen.makeID("ZhuanYunPower");
            if (AbstractDungeon.player.hasPower(zhuanYunId)) {
                ZhuanYunPower power = (ZhuanYunPower) AbstractDungeon.player.getPower(zhuanYunId);
                power.onProbabilityRollFailed(card);
            }
        }

        return success;
    }
}