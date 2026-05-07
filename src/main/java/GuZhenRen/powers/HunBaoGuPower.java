package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

public class HunBaoGuPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("HunBaoGuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private boolean isExploded = false;

    public HunBaoGuPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/HunBaoGuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/HunBaoGuPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
    }

    private void triggerExplosion() {
        if (this.isExploded) return;
        this.isExploded = true;

        this.flash();

        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (m != this.owner && !m.isDeadOrEscaped()) {
                AbstractDungeon.actionManager.addToTop(new DamageAction(
                        m,
                        new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS),
                        AbstractGameAction.AttackEffect.FIRE
                ));
            }
        }

        AbstractDungeon.actionManager.addToTop(new DamageAction(
                AbstractDungeon.player,
                new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS),
                AbstractGameAction.AttackEffect.NONE
        ));

        Color purple = Color.PURPLE.cpy();
        AbstractDungeon.actionManager.addToTop(new VFXAction(
                new ShockWaveEffect(this.owner.hb.cX, this.owner.hb.cY, purple, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.1F
        ));
        AbstractDungeon.actionManager.addToTop(new VFXAction(new BorderFlashEffect(purple, true)));
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (!this.isExploded && this.owner.currentHealth - damageAmount <= 0) {
            triggerExplosion();
        }
        return damageAmount;
    }

    @Override
    public void onDeath() {
        if (!this.isExploded) {
            triggerExplosion();
        }
    }
}