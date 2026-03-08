package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class XuePiaoLiuPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XuePiaoLiuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public XuePiaoLiuPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = false; // 杀招能力，整场战斗持续生效

        String pathLarge = GuZhenRen.assetPath("img/powers/XuePiaoLiuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XuePiaoLiuPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    // =========================================================================
    // 判定：仅在玩家回合内失去生命时触发抽牌
    // =========================================================================
    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        // 条件 1: 实际掉血量 > 0
        // 条件 2: 玩家回合尚未结束 (即处于玩家的行动阶段)
        if (damageAmount > 0 && !AbstractDungeon.actionManager.turnHasEnded) {
            this.flash();
            this.addToBot(new DrawCardAction(this.owner, this.amount));
        }
    }
}