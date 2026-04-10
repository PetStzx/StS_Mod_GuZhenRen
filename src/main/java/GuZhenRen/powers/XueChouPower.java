package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class XueChouPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XueChouPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public XueChouPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/XueChouPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XueChouPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);

        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    // =========================================================================
    // 判定 1：增加该敌人受到的伤害
    // =========================================================================
    @Override
    public float atDamageReceive(float damage, DamageType damageType) {
        // 仅翻倍普通的攻击伤害，避免被毒等异常状态无限放大
        if (damageType == DamageType.NORMAL) {
            return damage * 2.0F;
        }
        return damage;
    }

    // =========================================================================
    // 判定 2：增加该敌人造成的伤害
    // =========================================================================
    @Override
    public float atDamageGive(float damage, DamageType type) {
        if (type == DamageType.NORMAL) {
            return damage * 2.0F;
        }
        return damage;
    }
}