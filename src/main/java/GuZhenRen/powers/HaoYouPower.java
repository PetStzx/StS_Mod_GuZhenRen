package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HaoYouPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("HaoYouPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public HaoYouPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;

        String pathLarge = GuZhenRen.assetPath("img/powers/HaoYouPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/HaoYouPower.png");

        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = this.owner.name + DESCRIPTIONS[0];
    }

    // 减少目标对玩家造成的攻击伤害
    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * 0.5F;
        }
        return damage;
    }

    // 玩家对敌人造成攻击伤害时，减少层数
    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type != DamageInfo.DamageType.THORNS && info.type != DamageInfo.DamageType.HP_LOSS && info.owner != null && info.owner != this.owner) {
            if (info.output > 0) {
                this.flash();
                this.addToTop(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
            }
        }
        return damageAmount;
    }

    // 敌人对玩家造成攻击伤害时，减少层数
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type != DamageInfo.DamageType.THORNS && info.type != DamageInfo.DamageType.HP_LOSS && target != this.owner) {
            if (info.output > 0) {
                this.flash();
                this.addToTop(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
            }
        }
    }
}