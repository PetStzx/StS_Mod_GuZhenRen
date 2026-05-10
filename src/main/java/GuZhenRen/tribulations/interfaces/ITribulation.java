package GuZhenRen.tribulations.interfaces;

import com.megacrit.cardcrawl.powers.AbstractPower;

public interface ITribulation {
    String getId();
    String getName();
    String getDescription();
    String getTribulationType();

    // 0=玩家负面效果，1=更多敌人，2=增强敌人
    int getCategory();

    void atPreBattle(AbstractPower power);

    default void atStartOfTurn(AbstractPower power) {}
}