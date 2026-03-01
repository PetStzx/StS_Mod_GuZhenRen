package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction; // 【新增导入】
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class YiPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = GuZhenRen.makeID("YiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public YiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/YiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/YiPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        checkThreshold(); // 【修改】统一调用 checkThreshold
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        checkThreshold(); // 【修改】统一调用 checkThreshold
        updateDescription();
    }

    // 【新增】统一的阈值检测与销毁方法
    private void checkThreshold() {
        while (this.amount >= 3) {
            this.amount -= 3;
            triggerEffect();
        }
        // 如果转化后层数归零，立即将自己从状态栏移除
        if (this.amount <= 0) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    private void triggerEffect() {
        this.flash();
        // 回1费，获得1情
        this.addToBot(new GainEnergyAction(1));
        this.addToBot(new ApplyPowerAction(owner, owner, new QingPower(owner, 1), 1));
    }

    @Override
    public AbstractPower makeCopy() {
        return new YiPower(owner, amount);
    }
}