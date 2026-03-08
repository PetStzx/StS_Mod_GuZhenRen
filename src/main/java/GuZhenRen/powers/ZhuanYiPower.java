package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ZhuanYiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("ZhuanYiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ZhuanYiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/ZhuanYiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/ZhuanYiPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);

        if (largeTexture != null && smallTexture != null) {
            this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
            this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);
        } else {
            this.loadRegion("blur");
        }
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    // =========================================================================
    //  核心逻辑：给自己加格挡
    // =========================================================================
    public void triggerConversionBlock() {
        this.flash();
        this.addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
    }

    // =========================================================================
    //  专用信号接收器 (作为一个 Action，方便在任何地方被排入队列)
    // =========================================================================
    public static class TriggerAction extends AbstractGameAction {
        public TriggerAction() {
            this.actionType = ActionType.SPECIAL;
        }

        @Override
        public void update() {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(ZhuanYiPower.POWER_ID)) {
                // 如果玩家有转意蛊能力，调用它的核心逻辑叠甲
                ((ZhuanYiPower) AbstractDungeon.player.getPower(ZhuanYiPower.POWER_ID)).triggerConversionBlock();
            }
            this.isDone = true;
        }
    }
}