package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class WanWuDaTongBianPower extends AbstractPower implements OnReceivePowerPower {
    public static final String POWER_ID = GuZhenRen.makeID("WanWuDaTongBianPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public WanWuDaTongBianPower(AbstractCreature owner) {
        this.name = powerStrings.NAME;
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
        this.description = powerStrings.DESCRIPTIONS[0];
    }

    // =========================================================================
    // 功能 1: 获得时智能清洗全场（高拓展性版）
    // =========================================================================
    @Override
    public void onInitialApplication() {
        if (owner == null) return;

        int totalConvertAmount = 0;
        ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
        ArrayList<AbstractGameAction> reduceActions = new ArrayList<>(); // 用于部分扣除

        for (AbstractPower p : owner.powers) {
            // 1. 放行：万物大同变自己 和 所有道痕
            if (p.ID.equals(this.ID)) continue;
            if (p instanceof AbstractDaoHenPower) continue;

            // 2. 向所有道痕询问：这个状态你们要保几层？
            int protectedAmt = 0;
            for (AbstractPower daoHen : owner.powers) {
                if (daoHen instanceof AbstractDaoHenPower) {
                    protectedAmt += ((AbstractDaoHenPower) daoHen).getDerivedPowerAmount(p.ID);
                }
            }

            // 3. 计算实际能转化的层数 (-1的特殊状态视为1层)
            int currentAmt = (p.amount == -1) ? 1 : p.amount;
            int toConvert = currentAmt - protectedAmt;

            if (toConvert > 0) {
                totalConvertAmount += toConvert;

                // 如果完全没有被保护，或者本身就是没有层数(-1)的特殊状态，彻底移除
                if (protectedAmt == 0 || p.amount == -1) {
                    powersToRemove.add(p);
                } else {
                    // 如果有保护，只能削减“超出保护范围”的层数
                    reduceActions.add(new ReducePowerAction(owner, owner, p.ID, toConvert));
                }
            }
        }

        // 4. 执行结算
        if (totalConvertAmount > 0) {
            this.flash();
            this.addToTop(new ApplyPowerAction(owner, owner,
                    new BianHuaDaoDaoHenPower(owner, totalConvertAmount), totalConvertAmount));

            for (AbstractPower p : powersToRemove) {
                this.addToTop(new RemoveSpecificPowerAction(owner, owner, p));
            }
            for (AbstractGameAction action : reduceActions) {
                this.addToTop(action);
            }
        }
    }

    // =========================================================================
    // 功能 2: 拦截未来新状态
    // =========================================================================
    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // 放行免检状态与所有道痕
        if (AbstractDaoHenPower.isDerivedPower) return true;
        if (power instanceof AbstractDaoHenPower) return true;

        // 其余拦截并转化
        int convertAmt = (power.amount == -1) ? 1 : power.amount;
        if (convertAmt > 0) {
            this.flash();
            this.addToTop(new ApplyPowerAction(target, target,
                    new BianHuaDaoDaoHenPower(target, convertAmt), convertAmt));
        }
        return false;
    }
}