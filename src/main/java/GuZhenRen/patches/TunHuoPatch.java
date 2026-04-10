package GuZhenRen.patches;

import GuZhenRen.cards.HuoShi;
import GuZhenRen.powers.TunHuoPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class TunHuoPatch {

    // 补丁 1：拦截灼伤的伤害触发
    @SpirePatch(clz = Burn.class, method = "triggerOnEndOfTurnForPlayingCard")
    public static class MuteBurnDamagePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(Burn __instance) {
            // 如果玩家拥有吞火能力，阻断灼伤的伤害代码执行
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(TunHuoPower.POWER_ID)) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    // 补丁 2：监听所有卡牌进入消耗堆的动作
    @SpirePatch(clz = CardGroup.class, method = "moveToExhaustPile")
    public static class ExhaustBurnToHuoShiPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            // 判断进入消耗堆的牌是不是灼伤
            if (c.cardID.equals(Burn.ID)) {
                if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(TunHuoPower.POWER_ID)) {

                    int amount = AbstractDungeon.player.getPower(TunHuoPower.POWER_ID).amount;
                    AbstractDungeon.player.getPower(TunHuoPower.POWER_ID).flash(); // 能力闪烁

                    AbstractCard huoShi = new HuoShi();

                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(huoShi, amount));
                }
            }
        }
    }
}