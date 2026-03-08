package GuZhenRen.variables;

import GuZhenRen.cards.AbstractGuZhenRenCard;
import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class FenShaoVariable extends DynamicVariable {

    @Override
    public String key() {
        return "GuZhenRen:FenShao";
    }

    @Override
    public boolean isModified(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).isFenShaoModified;
        }
        return false;
    }

    @Override
    public int value(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).fenShao;
        }
        return 0;
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).baseFenShao;
        }
        return 0;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).upgradedFenShao;
        }
        return false;
    }
}