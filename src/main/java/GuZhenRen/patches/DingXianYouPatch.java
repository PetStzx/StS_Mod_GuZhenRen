package GuZhenRen.patches;

import GuZhenRen.GuZhenRen;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
        clz = MapRoomNode.class,
        method = "update"
)
public class DingXianYouPatch {
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                // 拦截 isConnectedTo 方法
                if (m.getMethodName().equals("isConnectedTo")) {

                    String relicID = "GuZhenRen:DingXianYou";

                    m.replace(
                            "$_ = ($proceed($$) || " +
                                    "(com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasRelic(\"" + relicID + "\") && " +
                                    "((com.megacrit.cardcrawl.map.MapRoomNode)$1).y == ((com.megacrit.cardcrawl.map.MapRoomNode)$0).y + 1" +
                                    "));"
                    );
                }
            }
        };
    }
}