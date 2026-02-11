package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ZhuiMingHuoPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("ZhuiMingHuoPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int FEN_SHAO_BASE = 5;

    public ZhuiMingHuoPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/ZhuiMingHuoPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/ZhuiMingHuoPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        if (largeTexture != null && smallTexture != null) {
            this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
            this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);
        } else {
            this.loadRegion("demonForm");
        }

        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        this.updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.flash();

        int totalBurn = FEN_SHAO_BASE * this.amount;

        // 传入 true (isPassive)，表示不吃道痕加成
        // ApplyPowerAction 的 source 是 this.owner (敌人自己)
        this.addToBot(new ApplyPowerAction(this.owner, this.owner,
                new FenShaoPower(this.owner, totalBurn, true), totalBurn));
    }

    @Override
    public void updateDescription() {
        int displayAmount = FEN_SHAO_BASE * this.amount;
        this.description = DESCRIPTIONS[0] + displayAmount + DESCRIPTIONS[1];
    }
}