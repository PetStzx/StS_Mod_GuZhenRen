package GuZhenRen.patches;

import GuZhenRen.relics.HongYunQiTianGu;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;

@SpirePatch(clz = AbstractRoom.class, method = "addRelicToRewards", paramtypez = {AbstractRelic.RelicTier.class})
public class RareRelicChancePatch {
    @SpirePrefixPatch
    public static void Prefix(AbstractRoom __instance, @ByRef AbstractRelic.RelicTier[] tier) {
        if (__instance instanceof MonsterRoom && AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(HongYunQiTianGu.ID)) {
            if (tier[0] != AbstractRelic.RelicTier.RARE && tier[0] != AbstractRelic.RelicTier.BOSS) {
                if (!AbstractDungeon.rareRelicPool.isEmpty()) {
                    if (AbstractDungeon.relicRng.randomBoolean(0.205f)) {
                        AbstractDungeon.player.getRelic(HongYunQiTianGu.ID).flash();
                        tier[0] = AbstractRelic.RelicTier.RARE;
                    }

                }
            }
        }
    }
}