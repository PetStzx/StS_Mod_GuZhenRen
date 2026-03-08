package GuZhenRen.variables;

import GuZhenRen.cards.AbstractGuZhenRenCard;
import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class NianVariable extends DynamicVariable {

    @Override
    public String key() {
        // 【标准规范】首字母大写
        return "GuZhenRen:Nian";
    }

    @Override
    public boolean isModified(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).isNianModified;
        }
        return false;
    }

    @Override
    public int value(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).nian;
        }
        return 0;
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).baseNian;
        }
        return 0;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).upgradedNian;
        }
        return false;
    }
}