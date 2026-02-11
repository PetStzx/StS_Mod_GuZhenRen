package GuZhenRen.variables;

import GuZhenRen.cards.AbstractGuZhenRenCard;
import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class SecondMagicNumber extends DynamicVariable {

    @Override
    public String key() {
        return "GuZhenRen:SecondMagic";
    }

    // =========================================================================
    //  【核心修改】 判断是否变色 (是否显示为绿色)
    // =========================================================================
    @Override
    public boolean isModified(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            // 只有当卡牌【在玩家手牌中】时，才允许显示为“已修改”状态
            if (AbstractDungeon.player != null && AbstractDungeon.player.hand.contains(card)) {
                return ((AbstractGuZhenRenCard) card).isSecondMagicNumberModified;
            }
        }
        // 如果不在手牌中（弃牌堆、抽牌堆、图鉴等），一律视为未修改（白色）
        return false;
    }

    // =========================================================================
    //  【核心修改】 获取当前数值
    // =========================================================================
    @Override
    public int value(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            // 1. 如果在手牌中，返回【当前值】（可能含有道痕加成）
            if (AbstractDungeon.player != null && AbstractDungeon.player.hand.contains(card)) {
                return ((AbstractGuZhenRenCard) card).secondMagicNumber;
            }

            // 2. 如果不在手牌中，强制返回【基础值】
            // 这样即使卡牌对象里存储的 secondMagicNumber 还是加成后的值，
            // 玩家在弃牌堆看到的也是基础值。
            return ((AbstractGuZhenRenCard) card).baseSecondMagicNumber;
        }
        return 0;
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).baseSecondMagicNumber;
        }
        return 0;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).upgradedSecondMagicNumber;
        }
        return false;
    }
}