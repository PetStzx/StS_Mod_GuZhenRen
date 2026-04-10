package GuZhenRen.patches;

import GuZhenRen.cards.AbstractShaZhaoCard;
import GuZhenRen.cards.ChengGongGu;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import basemod.ReflectionHacks;

@SpirePatch(
        clz = SingleCardViewPopup.class,
        method = "render"
)
public class BigCardVisualPatch {

    // 用于临时存储真实的稀有度
    private static AbstractCard.CardRarity originalRarity = null;

    @SpirePrefixPatch
    public static void Prefix(SingleCardViewPopup __instance, SpriteBatch sb) {
        // 1. 获取当前正在查看的卡牌对象
        AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");

        // 2. 判断是否是目标卡牌 (杀招 或 成功蛊)
        if (card instanceof AbstractShaZhaoCard || card instanceof ChengGongGu) {
            // 3. 记录真实稀有度 (SPECIAL)
            originalRarity = card.rarity;
            // 4. 伪装成金卡 (RARE)
            card.rarity = AbstractCard.CardRarity.RARE;
        }
    }

    @SpirePostfixPatch
    public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb) {
        // 1. 获取卡牌
        AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");

        // 2. 如果之前进行了伪装，现在还原回去
        if (originalRarity != null && (card instanceof AbstractShaZhaoCard || card instanceof ChengGongGu)) {
            card.rarity = originalRarity;
            originalRarity = null; // 重置标记
        }
    }
}