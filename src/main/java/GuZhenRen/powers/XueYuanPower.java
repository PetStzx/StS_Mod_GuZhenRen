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

    // 防止帧循环中重复排入移除动作的开关
    private boolean isRemoving = false;

    public XueYuanPower(AbstractCreature owner, int amount) {
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
        if (damageAmount > 0 && !this.isRemoving) {
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
    public void update(int slot) {
        super.update(slot);

        if (this.isRemoving || AbstractDungeon.getCurrRoom() == null || AbstractDungeon.getCurrRoom().monsters == null) {
            return;
        }

        if (AbstractDungeon.actionManager.actions.isEmpty() && AbstractDungeon.actionManager.currentAction == null) {

            boolean hasMarkedEnemy = false;
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                // 只要怪物还活着且有标记
                if (!mo.isDeadOrEscaped() && !mo.halfDead && mo.hasPower(XueYuanMarkPower.POWER_ID)) {
                    hasMarkedEnemy = true;
                    break;
                }
            }

            if (!hasMarkedEnemy) {
                this.isRemoving = true;
                AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
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