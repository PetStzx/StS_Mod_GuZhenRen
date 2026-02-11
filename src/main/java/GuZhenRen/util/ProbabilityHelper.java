package GuZhenRen.util;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class ProbabilityHelper {

    /**
     * 计算修正后的概率
     * 公式：(基础概率 + 加算修正) * 乘算修正
     * 结果限制在 [0, 1] 之间
     *
     * @param baseChance 基础概率 (例如 0.05f 代表 5%)
     * @return 最终概率
     */
    public static float getModifiedChance(float baseChance) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return baseChance;

        float additiveMod = 0.0f; // 加算池
        float multiplierMod = 1.0f; // 乘算池

        // ================================================================
        //  在下方添加未来的遗物/能力检测逻辑
        // ================================================================

        // 示例：检测是否有名为 "LuckyCharm" 的遗物，如果有，概率 +10%
        // if (p.hasRelic("GuZhenRen:LuckyCharm")) {
        //     additiveMod += 0.1f;
        // }

        // 示例：检测是否有名为 "SuperLuck" 的能力，如果有，概率 x3
        // if (p.hasPower("GuZhenRen:SuperLuckPower")) {
        //     multiplierMod *= 3.0f;
        // }

        // ================================================================
        //  计算最终结果
        // ================================================================
        float finalChance = (baseChance + additiveMod) * multiplierMod;

        // 截断上下限 (0.0 - 1.0)
        if (finalChance > 1.0f) finalChance = 1.0f;
        if (finalChance < 0.0f) finalChance = 0.0f;

        return finalChance;
    }
}