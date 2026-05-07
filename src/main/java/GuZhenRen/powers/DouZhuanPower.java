package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.SanctityEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect.ShockWaveType;


public class DouZhuanPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("DouZhuanPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int lastHp = -1;

    public DouZhuanPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/DouZhuanPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/DouZhuanPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }


    // 获得时，检测并修改坚不可摧
    @Override
    public void onInitialApplication() {
        if (this.owner != null && this.owner.hasPower(InvinciblePower.POWER_ID)) {
            AbstractPower invincible = this.owner.getPower(InvinciblePower.POWER_ID);

            // 计算最大生命值的三分之二
            float rawCap = this.owner.maxHealth * 2.0f / 3.0f;
            // 向下取整到 10 的倍数
            int newCap = ((int) rawCap / 10) * 10;

            if (newCap < 10) newCap = 10;

            ReflectionHacks.setPrivate(invincible, InvinciblePower.class, "maxAmt", newCap);
            invincible.amount = newCap;
            invincible.updateDescription();
        }
    }


    @Override
    public void atStartOfTurn() {
        if (this.owner == null || this.owner.isDeadOrEscaped() || this.owner.halfDead) {
            return;
        }

        int currentHp = this.owner.currentHealth;
        int lostHp = this.owner.maxHealth - currentHp;

        if (currentHp < lostHp) {
            this.flash();

            AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                @Override
                public void update() {
                    CardCrawlGame.sound.playV("POWER_SHACKLE", 1.2F);
                    CardCrawlGame.sound.playV("BELL", 0.5F);

                    AbstractDungeon.effectList.add(new BorderFlashEffect(Color.GOLD.cpy(), true));
                    AbstractDungeon.effectList.add(new SanctityEffect(owner.hb.cX, owner.hb.cY));
                    AbstractDungeon.effectList.add(new ShockWaveEffect(
                            owner.hb.cX, owner.hb.cY, Color.GOLD.cpy(), ShockWaveType.NORMAL));

                    owner.currentHealth = lostHp;
                    owner.healthBarUpdatedEvent();

                    this.isDone = true;
                }
            });
        }
    }


    @Override
    public void updateDescription() {
        if (this.owner != null) {
            int currentHp = this.owner.currentHealth;
            int lostHp = this.owner.maxHealth - currentHp;
            this.description = DESCRIPTIONS[0] + " #b" + currentHp + DESCRIPTIONS[1] + " #b" + lostHp + DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + " #b? " + DESCRIPTIONS[1] + " #b? " + DESCRIPTIONS[2];
        }
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if (this.owner != null && this.lastHp != this.owner.currentHealth) {
            this.lastHp = this.owner.currentHealth;
            this.updateDescription();
        }
    }
}