package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TaiGuRongYaoZhiGuangPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("TaiGuRongYaoZhiGuangPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 【修改】每层增加 25% 受到光道牌的伤害
    private static final float MULTIPLIER_PER_STACK = 0.25F;

    public TaiGuRongYaoZhiGuangPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF; // 负面效果
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/TaiGuRongYaoZhiGuangPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/TaiGuRongYaoZhiGuangPower.png");

        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);

        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        if (card != null && card.hasTag(GuZhenRenTags.GUANG_DAO) && type == DamageInfo.DamageType.NORMAL) {
            // 计算倍率：1 + (层数 * 0.25)
            // 2层 = 1.5倍 (+50%)
            // 6层 = 2.5倍 (+150%)
            float multiplier = 1.0F + (this.amount * MULTIPLIER_PER_STACK);
            return damage * multiplier;
        }
        return damage;
    }

    @Override
    public void updateDescription() {
        // 计算百分比：层数 * 25
        int percentage = (int)(this.amount * MULTIPLIER_PER_STACK * 100);
        this.description = DESCRIPTIONS[0] + percentage + DESCRIPTIONS[1];
    }
}