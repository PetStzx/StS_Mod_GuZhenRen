package GuZhenRen.util;
import com.megacrit.cardcrawl.cards.AbstractCard;

public interface IProbabilityModifier {
    // 1. 基础乘算 (默认1.0)
    default float getBaseProbabilityMultiplier(AbstractCard card) { return 1.0f; }

    // 2. 加算 (默认0.0)
    default float getAdditiveProbability(AbstractCard card) { return 0.0f; }

    // 3. 最终乘算 (默认1.0)
    default float getFinalProbabilityMultiplier(AbstractCard card) { return 1.0f; }

    // 4. 绝对覆盖 (如全力以赴蛊的 100% 生效，返回负数代表不覆盖)
    default float getAbsoluteProbabilityOverride(AbstractCard card) { return -1.0f; }
}