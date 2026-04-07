package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class QingPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("QingPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public QingPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/QingPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/QingPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    // =========================================================================
    // 文本动态更新：括号里显示当前能抽几张
    // =========================================================================
    @Override
    public void updateDescription() {
        // 计算额外抽牌的数量：向下取整 (每 3 层抽 1 张)
        int extraDraw = this.amount / 3;
        this.description = DESCRIPTIONS[0] + extraDraw + DESCRIPTIONS[1];
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        this.updateDescription();
    }

    // =========================================================================
    // 核心触发逻辑：回合开始、正常抽牌结束后触发
    // =========================================================================
    @Override
    public void atStartOfTurnPostDraw() {
        int extraDraw = this.amount / 3;

        // 如果满足了至少 3 层，就执行抽牌动作
        if (extraDraw > 0) {
            this.flash(); // 闪烁能力图标提示玩家
            this.addToBot(new DrawCardAction(this.owner, extraDraw));
        }
    }
}