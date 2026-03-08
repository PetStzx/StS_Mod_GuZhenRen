package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class XueLiuBuZhiPower extends AbstractPower implements HealthBarRenderPower {
    public static final String POWER_ID = GuZhenRen.makeID("XueLiuBuZhiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Color DARK_RED = new Color(0.25F, 0.0F, 0.0F, 1.0F);
    public XueLiuBuZhiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF; // 负面效果
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/XueLiuBuZhiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XueLiuBuZhiPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);

        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    // =========================================================================
    // 触发条件：回合开始时失去生命
    // =========================================================================
    @Override
    public void atStartOfTurn() {
        this.flash();
        this.addToBot(new LoseHPAction(this.owner, this.owner, this.amount));
    }

    // =========================================================================
    // StSLib 血条渲染接口方法
    // =========================================================================

    // 决定要在血条上渲染出多少掉血量
    @Override
    public int getHealthBarAmount() {
        // 因为我们每回合扣除等同于层数的血量，所以直接返回 amount
        return this.amount;
    }

    // 决定血条预测扣除部分的颜色
    @Override
    public Color getColor() {
        return DARK_RED;
    }
}