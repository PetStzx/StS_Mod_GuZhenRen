package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class AbstractDaoHenPower extends AbstractPower {

    public static boolean isDerivedPower = false;

    public AbstractDaoHenPower(String id, String name, AbstractCreature owner, int amount) {
        this.ID = id;
        this.name = name;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String unPrefixedId = id.replace(GuZhenRen.MOD_ID + ":", "");
        String pathLarge = GuZhenRen.assetPath("img/powers/" + unPrefixedId + "_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/" + unPrefixedId + ".png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);
    }


    // 提供给子类重写：声明你需要保护的“伴生状态”层数
    public int getDerivedPowerAmount(String powerID) {
        return 0; // 默认没有伴生状态需要保护
    }

    @Override
    public void atStartOfTurn() {
        if (this.owner.isPlayer && !this.ID.equals(BianHuaDaoDaoHenPower.POWER_ID)) {
            this.flash();
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            this.addToBot(new ApplyPowerAction(this.owner, this.owner,
                    new BianHuaDaoDaoHenPower(this.owner, this.amount), this.amount));
            this.addToBot(new ZhuanYiPower.TriggerAction());
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        this.updateDescription();
    }

    @Override
    public void onInitialApplication() {
        this.updateDescription();
    }
}