package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.BattleStateManager;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.WeakHashMap;

public class NiLiuHe extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("NiLiuHe");
    private static final String IMG = GuZhenRen.assetPath("img/relics/NiLiuHe.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/NiLiuHe.png");

    private static final int MAX_WATER = 9;

    // 核心联动标记：记录当前正在被逆流河反噬的怪物
    public static AbstractCreature reflectingTarget = null;

    // 动作处理器记录：确保动作只被拦截判定一次
    public static WeakHashMap<AbstractGameAction, Boolean> processedActions = new WeakHashMap<>();

    // ==========================================================
    // 生命周期管理：解决 SL 与快速重置导致的静态变量残留与内存泄漏
    // ==========================================================
    static {
        BattleStateManager.onBattleStart(() -> {
            NiLiuHe.reflectingTarget = null;
            NiLiuHe.processedActions.clear();
        });

        BattleStateManager.onPostBattle(() -> {
            NiLiuHe.reflectingTarget = null;
            NiLiuHe.processedActions.clear();
        });
    }

    public NiLiuHe() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.SPECIAL, LandingSound.MAGICAL);
        this.counter = 3;
        this.updateDescriptionDynamically();
    }

    @Override
    public String getUpdatedDescription() {
        int displayCount = Math.max(0, this.counter);
        return String.format(DESCRIPTIONS[0], displayCount);
    }

    public void updateDescriptionDynamically() {
        this.description = this.getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if (this.counter < MAX_WATER) {
            this.counter++;
            this.updateDescriptionDynamically();
            this.flash();
        }
    }

    @Override
    public void atTurnStart() {
        // 玩家回合开始时，自然清理上一轮的逆反标记，防止跨回合污染
        reflectingTarget = null;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new NiLiuHe();
    }

    // ==========================================================
    // 拦截器 1：伤害逆反
    // ==========================================================
    @SpirePatch(clz = DamageAction.class, method = "update")
    public static class DamagePatch {
        @SpirePrefixPatch
        public static void Prefix(DamageAction __instance) {
            if (processedActions.getOrDefault(__instance, false)) {
                return;
            }
            processedActions.put(__instance, true);

            if (__instance.target == AbstractDungeon.player) {
                DamageInfo info = ReflectionHacks.getPrivate(__instance, DamageAction.class, "info");

                if (info != null && info.type == DamageInfo.DamageType.NORMAL && __instance.source != null && !__instance.source.isPlayer) {
                    if (AbstractDungeon.player.hasRelic(NiLiuHe.ID)) {
                        NiLiuHe relic = (NiLiuHe) AbstractDungeon.player.getRelic(NiLiuHe.ID);

                        if (relic.counter > 0) {
                            relic.counter--;
                            relic.updateDescriptionDynamically();
                            relic.flash();

                            // 将伤害目标强制改为怪物自己
                            __instance.target = __instance.source;

                            // 锁定该怪物！它接下来的附带 Debuff 也会被反弹
                            NiLiuHe.reflectingTarget = __instance.source;
                        } else {
                            // 防跨次污染：没水了，清除逆反标记
                            NiLiuHe.reflectingTarget = null;
                        }
                    } else {
                        NiLiuHe.reflectingTarget = null;
                    }
                }
            }
        }
    }

    // ==========================================================
    // 拦截器 2：附带 Debuff 逆反
    // ==========================================================
    @SpirePatch(clz = ApplyPowerAction.class, method = "update")
    public static class ApplyPowerPatch {
        @SpirePrefixPatch
        public static void Prefix(ApplyPowerAction __instance) {
            if (processedActions.getOrDefault(__instance, false)) {
                return;
            }
            processedActions.put(__instance, true);

            // 如果目标是玩家，且施加状态的怪物恰好是我们刚刚标记的被反噬者
            if (__instance.target == AbstractDungeon.player) {
                if (__instance.source != null && __instance.source == NiLiuHe.reflectingTarget) {
                    AbstractPower powerToApply = ReflectionHacks.getPrivate(__instance, ApplyPowerAction.class, "powerToApply");

                    if (powerToApply != null && powerToApply.type == AbstractPower.PowerType.DEBUFF) {

                        // 将目标和所有权全都转交还给怪物自身
                        __instance.target = __instance.source;
                        powerToApply.owner = __instance.source;

                        if (AbstractDungeon.player.hasRelic(NiLiuHe.ID)) {
                            AbstractDungeon.player.getRelic(NiLiuHe.ID).flash();
                        }
                    }
                }
            }
        }
    }
}