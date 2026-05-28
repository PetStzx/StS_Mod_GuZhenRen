package GuZhenRen.patches;

import GuZhenRen.cards.GuQiangGu;
import GuZhenRen.cards.LuoXuanGuQiangGu;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;

@SpirePatch(
        clz = ShopRoom.class,
        method = "onPlayerEntry"
)
public class ShopAutoRemovePatch {
    @SpirePostfixPatch
    public static void Postfix(ShopRoom __instance) {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) return;

        // 寻找玩家牌库中所有需要被商店回收的特定事件牌
        ArrayList<AbstractCard> cardsToRemove = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.cardID.equals(GuQiangGu.ID) || c.cardID.equals(LuoXuanGuQiangGu.ID)) {
                cardsToRemove.add(c);
            }
        }

        if (!cardsToRemove.isEmpty()) {
            int totalGold = 0;
            for (AbstractCard c : cardsToRemove) {
                totalGold += c.magicNumber;
                AbstractDungeon.player.masterDeck.removeCard(c);

                float x = MathUtils.random(Settings.WIDTH * 0.4F, Settings.WIDTH * 0.6F);
                float y = MathUtils.random(Settings.HEIGHT * 0.4F, Settings.HEIGHT * 0.6F);
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, x, y));
            }

            AbstractDungeon.player.gainGold(totalGold);
            AbstractDungeon.effectList.add(new RainingGoldEffect(totalGold));
        }
    }
}