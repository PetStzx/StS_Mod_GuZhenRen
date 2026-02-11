package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class YanDaoDaoHenPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("YanDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public boolean isFromBianHua = false;

    public YanDaoDaoHenPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/YanDaoDaoHenPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/YanDaoDaoHenPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        int bonus = this.amount / 2;
        // 这里的拼接逻辑依赖 JSON 中第一段的末尾不带空格，或者 #b 紧贴末尾
        this.description = DESCRIPTIONS[0] + bonus + DESCRIPTIONS[1];
    }

    // 【新增】 首次获得时（ApplyPowerAction 执行时）强制刷新一次描述
    // 这能解决“BianHuaDaoDaoHenPower”修改了 amount 但没刷新描述的问题
    @Override
    public void onInitialApplication() {
        this.updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        this.updateDescription();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && this.isFromBianHua) {
            this.flash();
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            this.addToBot(new ApplyPowerAction(this.owner, this.owner,
                    new BianHuaDaoDaoHenPower(this.owner, this.amount), this.amount));
        }
    }
}