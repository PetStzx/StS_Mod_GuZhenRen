package GuZhenRen.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(clz = AbstractPlayer.class, method = "useCard")
public class YiLuanPatch {

    @SpireInstrumentPatch
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("use")) {
                    m.replace(
                            "boolean yiLuanTriggered = false;" +
                                    "if ($0 instanceof GuZhenRen.cards.AbstractShaZhaoCard) {" +
                                    "    for (int i = 0; i < com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hand.group.size(); i++) {" +
                                    "        com.megacrit.cardcrawl.cards.AbstractCard c = (com.megacrit.cardcrawl.cards.AbstractCard)com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hand.group.get(i);" +
                                    "        if (c instanceof GuZhenRen.cards.YiLuan) {" +
                                    "            if (((GuZhenRen.cards.YiLuan)c).tryTriggerFailure()) {" +
                                    "                yiLuanTriggered = true;" +
                                    "                break;" +
                                    "            }" +
                                    "        }" +
                                    "    }" +
                                    "}" +
                                    "if (yiLuanTriggered) {" +
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