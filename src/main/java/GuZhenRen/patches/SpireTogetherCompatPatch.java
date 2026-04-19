package GuZhenRen.patches;

import GuZhenRen.character.NetworkFangYuan;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import spireTogether.SpireTogetherMod;

public class SpireTogetherCompatPatch {
    @SpirePatch(clz = SpireTogetherMod.class, method = "RegisterModdedChars", optional = true)
    public static class FangYuanRegisterPatch {
        public static void Postfix() {
            if (SpireTogetherMod.allCharacterEntities != null) {
                SpireTogetherMod.allCharacterEntities.put(AbstractPlayerEnum.FANG_YUAN, new NetworkFangYuan());
            }
        }
    }
}