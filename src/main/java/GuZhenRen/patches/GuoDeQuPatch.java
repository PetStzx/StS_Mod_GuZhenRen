package GuZhenRen.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(clz = AbstractPlayer.class, method = "useCard")
public class GuoDeQuPatch {

    @SpireInstrumentPatch
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("use")) {
                    m.replace(
                            "if (GuZhenRen.powers.GuoDeQuPower.getActiveGuoDeQu() != null) {" +
                                    "    GuZhenRen.powers.GuoDeQuPower.getActiveGuoDeQu().triggerNullify();" +
                                    "    " +
                                    "    /* 处理 X费 卡牌的耗能逻辑 */" +
                                    "    if ($0.cost == -1 && !$0.freeToPlayOnce && !$0.ignoreEnergyOnUse) {" +
                                    "        com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.energy.use(com.megacrit.cardcrawl.ui.panels.EnergyPanel.totalCount);" +
                                    "    }" +
                                    "} else {" +
                                    "    $proceed($$);" +
                                    "}"
                    );
                }
            }
        };
    }
}