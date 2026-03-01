package GuZhenRen.util;

public interface IProbabilityCard {
    // 增加卡牌的基础概率
    void increaseBaseChance(float amount);
    // 获取当前的基础概率（可选，用于UI显示等）
    float getBaseChance();
}