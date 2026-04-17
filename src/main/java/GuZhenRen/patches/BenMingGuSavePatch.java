package GuZhenRen.patches;

import GuZhenRen.character.FangYuan;
import GuZhenRen.effects.BenMingGuOpeningEffect;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

@SpirePatch(
        clz = NeowEvent.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = {boolean.class}
)
public class BenMingGuSavePatch {

    @SpirePostfixPatch
    public static void Postfix(NeowEvent __instance, boolean isDone) {

        if (AbstractDungeon.floorNum > 0) {
            return;
        }

        if (isDone) {
            return;
        }

        // 1. 安全检查
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
            return;
        }

        // 2. 检查角色是否为“古月方源”
        if (!(AbstractDungeon.player instanceof FangYuan)) {
            return;
        }

        // 3. 检查玩家是否已经拥有本命蛊
        boolean hasBenMingGu = false;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.hasTag(GuZhenRenTags.BEN_MING_GU)) {
                hasBenMingGu = true;
                break;
            }
        }

        if (hasBenMingGu) {
            return;
        }

        // 4. 检查特效队列，防止重复添加
        // 检查普通队列
        for (AbstractGameEffect e : AbstractDungeon.effectList) {
            if (e instanceof BenMingGuOpeningEffect) {
                return;
            }
        }
        // 检查顶层队列
        for (AbstractGameEffect e : AbstractDungeon.topLevelEffects) {
            if (e instanceof BenMingGuOpeningEffect) {
                return;
            }
        }
        for (AbstractGameEffect e : AbstractDungeon.topLevelEffectsQueue) {
            if (e instanceof BenMingGuOpeningEffect) {
                return;
            }
        }

        // 5. 添加到顶层特效队列
        AbstractDungeon.topLevelEffectsQueue.add(new BenMingGuOpeningEffect());
    }
}