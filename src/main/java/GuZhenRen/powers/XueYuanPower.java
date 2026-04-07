package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.OfferingEffect;

public class XueYuanPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XueYuanPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public XueYuanPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/XueYuanPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XueYuanPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        // 移除了 !AbstractDungeon.actionManager.turnHasEnded 的判定
        // 现在只要实际失去生命（damageAmount > 0），无论谁的回合都会触发
        if (damageAmount > 0) {
            this.flash();
            this.addToBot(new VFXAction(new OfferingEffect(), 0.1F));

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped() && mo.hasPower(XueYuanMarkPower.POWER_ID)) {
                    int multi = mo.getPower(XueYuanMarkPower.POWER_ID).amount;
                    this.addToBot(new LoseHPAction(mo, this.owner, damageAmount * multi));
                }
            }
        }
    }

    @Override
    public void onRemove() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo.hasPower(XueYuanMarkPower.POWER_ID)) {
                this.addToTop(new RemoveSpecificPowerAction(mo, this.owner, XueYuanMarkPower.POWER_ID));
            }
        }
    }
}