package GuZhenRen.variables;

import GuZhenRen.cards.AbstractGuZhenRenCard;
import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class SecondMagicNumber extends DynamicVariable {

    @Override
    public String key() {
        return "GuZhenRen:SecondMagic";
    }

    @Override
    public boolean isModified(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            // 战斗中，只有吃到Buff或在升级预览界面时，才会变色
            return ((AbstractGuZhenRenCard) card).isSecondMagicNumberModified;
        }
        return false;
    }

    @Override
    public int value(AbstractCard card) {
        if (card instanceof AbstractGuZhenRenCard) {
            return ((AbstractGuZhenRenCard) card).secondMagicNumber;
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

            return ((AbstractGuZhenRenCard) card).isSecondMagicNumberModified;
        }
        return false;
    }
}