package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction; // 【新增】导入移除能力的动作
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class NianTouShouZuPower extends AbstractPower implements OnReceivePowerPower {
    public static final String POWER_ID = GuZhenRen.makeID("NianTouShouZuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public NianTouShouZuPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;

        this.type = PowerType.DEBUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/NianTouShouZuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/NianTouShouZuPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    // 首次获得该能力时触发：清空身上的“念”
    @Override
    public void onInitialApplication() {
        if (this.owner.hasPower(NianPower.POWER_ID)) {
            this.flash();
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, NianPower.POWER_ID));
        }
    }

    // 重复获得该能力时触发：防范边缘情况，依然清空“念”
    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (this.owner.hasPower(NianPower.POWER_ID)) {
            this.flash();
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, NianPower.POWER_ID));
        }
    }

    //  接口实现：阻断后续获得念
    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (power instanceof NianPower) {
            // 如果玩家拥有“智障”，则优先让“智障”去处理转化。
            if (owner.hasPower(ZhiZhangPower.POWER_ID)) {
                return true;
            }

            // 如果没有智障，则执行阻断逻辑
            this.flash(); // 闪烁图标
            return false; // 拦截，不获得念
        }
        return true;
    }
}