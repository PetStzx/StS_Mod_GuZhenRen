package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class WanXingFeiYingPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("WanXingFeiYingPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int nianBaseAmount; // 基础念数量

    public WanXingFeiYingPower(AbstractCreature owner, int turns, int nianBaseAmount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = turns; // amount 代表剩余回合数
        this.nianBaseAmount = nianBaseAmount;
        this.type = PowerType.BUFF;
        this.isTurnBased = true;

        // 图片加载逻辑 (带安全检查)
        String pathLarge = GuZhenRen.assetPath("img/powers/WanXingFeiYingPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/WanXingFeiYingPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);

        if (largeTexture == null || smallTexture == null) {
            // 如果没图，暂时用“星念”的图或者其他替代
            pathLarge = GuZhenRen.assetPath("img/powers/XingNianGuPower_p.png");
            pathSmall = GuZhenRen.assetPath("img/powers/XingNianGuPower.png");
            largeTexture = ImageMaster.loadImage(pathLarge);
            smallTexture = ImageMaster.loadImage(pathSmall);
        }

        if (largeTexture != null && smallTexture != null) {
            this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
            this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);
        } else {
            this.loadRegion("starlight"); // 原版备用图标
        }

        updateDescription();
    }

    // =========================================================================
    //  核心效果：每打出一张牌，获得念
    // =========================================================================
    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        this.flash();
        // 关键点：我们传入 nianBaseAmount (1)
        // new NianPower(owner, 1) 会在其构造函数中自动读取玩家身上的【情】和【智道道痕】
        // 从而加上对应的 Bonus。
        this.addToBot(new ApplyPowerAction(owner, owner,
                new NianPower(owner, this.nianBaseAmount), this.nianBaseAmount));
    }

    // =========================================================================
    //  回合递减逻辑
    // =========================================================================
    @Override
    public void atEndOfRound() {
        this.amount--;
        if (this.amount == 0) {
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        } else {
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        // 描述示例： "每当你打出1张牌，获得 #b1 点念。持续 #b3 回合。"
        // DESCRIPTIONS[0] = "每当你打出1张牌，获得 #b"
        // DESCRIPTIONS[1] = " 点念。 NL 持续 #b"
        // DESCRIPTIONS[2] = " 回合。"
        this.description = DESCRIPTIONS[0] + this.nianBaseAmount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }
}