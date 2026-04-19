package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
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

    public WanWuDaTongBianPower(AbstractCreature owner, int amount) {
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

    // 功能 1: 获得时清洗全场
    @Override
    public void onInitialApplication() {
        if (owner == null) return;

        int totalConvertAmount = 0;
        ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
        ArrayList<AbstractGameAction> adjustActions = new ArrayList<>();

        for (AbstractPower p : owner.powers) {
            // 1. 放行：万物大同变自己 和 所有道痕
            if (p.ID.equals(this.ID)) continue;
            if (p instanceof AbstractDaoHenPower) continue;

            // 2. 统计所有道痕对该状态的保护层数
            int protectedAmt = 0;
            for (AbstractPower daoHen : owner.powers) {
                if (daoHen instanceof AbstractDaoHenPower) {
                    protectedAmt += ((AbstractDaoHenPower) daoHen).getDerivedPowerAmount(p.ID);
                }
            }

            int currentAmt = p.amount;

            if (currentAmt == -1) {
                // 纯机制状态
                int toConvert = 1 - protectedAmt;
                if (toConvert > 0) {
                    totalConvertAmount += toConvert;
                    if (protectedAmt <= 0) {
                        powersToRemove.add(p);
                    }
                }
            } else {
                // 有具体数值的状态
                if (currentAmt < protectedAmt) {
                    totalConvertAmount += 1;

                    if (protectedAmt == 0) {
                        powersToRemove.add(p);
                    } else {
                        adjustActions.add(new SetPowerAmountAction(p, protectedAmt));
                    }
                } else if (currentAmt > protectedAmt) {
                    int toConvert = currentAmt - protectedAmt;
                    totalConvertAmount += toConvert;

                    if (protectedAmt == 0) {
                        powersToRemove.add(p);
                    } else {
                        adjustActions.add(new SetPowerAmountAction(p, protectedAmt));
                    }
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
            for (AbstractGameAction action : adjustActions) {
                this.addToTop(action);
            }
        }
    }

    // 功能 2: 拦截未来新状态
    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (AbstractDaoHenPower.isDerivedPower) return true;
        if (power instanceof AbstractDaoHenPower) return true;

        int convertAmt = (power.amount <= 0) ? 1 : power.amount;

        this.flash();
        this.addToTop(new ApplyPowerAction(target, target,
                new BianHuaDaoDaoHenPower(target, convertAmt), convertAmt));

        return false;
    }


    private static class SetPowerAmountAction extends AbstractGameAction {
        private final AbstractPower power;
        private final int targetAmount;

        public SetPowerAmountAction(AbstractPower power, int targetAmount) {
            this.power = power;
            this.targetAmount = targetAmount;
            this.actionType = ActionType.REDUCE_POWER;
        }

        @Override
        public void update() {
            this.power.amount = this.targetAmount;
            this.power.updateDescription();
            this.isDone = true;
        }
    }
}