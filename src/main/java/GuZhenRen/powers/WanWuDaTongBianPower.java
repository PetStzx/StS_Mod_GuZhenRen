package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import basemod.ReflectionHacks;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class WanWuDaTongBianPower extends AbstractPower implements OnReceivePowerPower {
    public static final String POWER_ID = GuZhenRen.makeID("WanWuDaTongBianPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public WanWuDaTongBianPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/WanWuDaTongBianPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/WanWuDaTongBianPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    // =========================================================================
    //  功能 1: 获得该能力时，转化已有的所有状态
    // =========================================================================
    @Override
    public void onInitialApplication() {
        if (owner == null) return;

        int totalConvertAmount = 0;
        ArrayList<AbstractPower> powersToRemove = new ArrayList<>();

        // 1. 遍历当前身上的所有 Buff/Debuff
        for (AbstractPower p : owner.powers) {
            // 跳过自己
            if (p.ID.equals(POWER_ID)) continue;
            // 跳过变化道道痕
            if (p.ID.equals(BianHuaDaoDaoHenPower.POWER_ID)) continue;
            // 跳过由变化道变出去的道痕 (防止把刚变好的智道又变回来)
            if (isFromBianHua(p)) continue;

            // 2. 计算转化层数
            // 如果 amount 为 -1 (唯一状态)，则视为 1 层
            int convertAmt = (p.amount == -1) ? 1 : p.amount;
            if (convertAmt > 0) {
                totalConvertAmount += convertAmt;
                powersToRemove.add(p);
            }
        }

        // 3. 执行转化
        if (totalConvertAmount > 0) {
            this.flash();
            // 先添加变化道道痕
            this.addToTop(new ApplyPowerAction(owner, owner,
                    new BianHuaDaoDaoHenPower(owner, totalConvertAmount), totalConvertAmount));

            // 再移除旧状态 (倒序移除比较安全，虽然有 RemoveSpecific 也没事)
            for (AbstractPower p : powersToRemove) {
                this.addToTop(new RemoveSpecificPowerAction(owner, owner, p));
            }
        }
    }

    // =========================================================================
    //  功能 2: 拦截未来获得的状态
    // =========================================================================
    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // 1. 过滤逻辑
        if (power.ID.equals(WanWuDaTongBianPower.POWER_ID)) return true;
        if (power.ID.equals(BianHuaDaoDaoHenPower.POWER_ID)) return true;
        if (isFromBianHua(power)) return true;

        // 2. 计算转化层数
        // 【核心修复】 处理 amount = -1 的情况
        int convertAmt = (power.amount == -1) ? 1 : power.amount;

        // 3. 执行转化逻辑
        if (convertAmt > 0) {
            this.flash();
            // 拦截并转化为变化道
            this.addToTop(new ApplyPowerAction(target, target,
                    new BianHuaDaoDaoHenPower(target, convertAmt), convertAmt));
        }

        // 4. 返回 false 表示拦截原始状态
        return false;
    }

    // =========================================================================
    //  辅助工具: 检查是否是“由变化道转化而来”
    // =========================================================================
    private boolean isFromBianHua(AbstractPower power) {
        try {
            // 优先尝试直接获取字段（支持 public）
            Field f = power.getClass().getField("isFromBianHua");
            return f.getBoolean(power);
        } catch (NoSuchFieldException e1) {
            // 备用：尝试获取 private 字段
            try {
                return ReflectionHacks.getPrivate(power, power.getClass(), "isFromBianHua");
            } catch (Exception e2) {
                // 没有这个字段，说明不是相关道痕
                return false;
            }
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return false;
        }
    }
}