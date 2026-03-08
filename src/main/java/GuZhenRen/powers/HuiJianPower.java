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

public class HuiJianPower extends AbstractPower implements OnReceivePowerPower {
    public static final String POWER_ID = GuZhenRen.makeID("HuiJianPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public HuiJianPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1; // -1 表示这是一个不可叠加层数的状态类能力
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/HuiJianPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/HuiJianPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    // =========================================================================
    //  功能 1: 获得《慧剑》时，斩断现有的《情》
    // =========================================================================
    @Override
    public void onInitialApplication() {
        if (this.owner.hasPower(QingPower.POWER_ID)) {
            int qingAmount = this.owner.getPower(QingPower.POWER_ID).amount;

            if (qingAmount > 0) {
                this.flash();
                // 移除现有的“情”
                this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, QingPower.POWER_ID));
                // 转化为“剑锋”
                this.addToTop(new ApplyPowerAction(this.owner, this.owner, new JianFengPower(this.owner, qingAmount), qingAmount));
                this.addToBot(new ZhuanYiPower.TriggerAction());
            }
        }
    }

    // =========================================================================
    //  功能 2: 拦截未来获得的《情》
    // =========================================================================
    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // 判定：如果即将获得的是“情”
        if (power.ID.equals(QingPower.POWER_ID)) {
            this.flash();

            int convertAmt = power.amount;
            if (convertAmt > 0) {
                // 将“情”的层数转化为等量的“剑锋” (使用 addToTop 确保立即转化生效)
                this.addToTop(new ApplyPowerAction(target, target, new JianFengPower(target, convertAmt), convertAmt));
                this.addToBot(new ZhuanYiPower.TriggerAction());
            }

            // 返回 false，表示拦截原始状态，不再获得“情”
            return false;
        }

        return true;
    }
}