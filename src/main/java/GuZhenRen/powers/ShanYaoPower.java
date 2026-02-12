package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction; // 改用移除动作
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ShanYaoPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("ShanYaoPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 每层增加 50% 伤害
    private static final float MULTIPLIER_PER_STACK = 0.5F;

    public ShanYaoPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/ShanYaoPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/ShanYaoPower.png");

        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);

        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    // 【核心逻辑】修改伤害
    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        // 只有带有 GUANG_DAO 标签的卡牌享受加成
        if (card.hasTag(GuZhenRenTags.GUANG_DAO)) {
            // 计算倍率：1 + (层数 * 0.5)
            // 1层 = 1.5倍 (+50%)
            // 2层 = 2.0倍 (+100%)
            float multiplier = 1.0F + (this.amount * MULTIPLIER_PER_STACK);
            return damage * multiplier;
        }
        return damage;
    }

    @Override
    public void onUseCard(AbstractCard card, com.megacrit.cardcrawl.actions.utility.UseCardAction action) {
        // 只有使用了带有 GUANG_DAO 标签的 攻击牌，才消耗
        // 技能牌(如小光蛊自己)虽然也是光道，但不应该消耗这个增伤Buff
        if (card.hasTag(GuZhenRenTags.GUANG_DAO) && card.type == AbstractCard.CardType.ATTACK) {
            this.flash();
            // 【修改】消耗全部层数 (移除BUFF)
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    // 更新描述
    @Override
    public void updateDescription() {
        // 计算百分比：层数 * 50
        int percentage = (int)(this.amount * MULTIPLIER_PER_STACK * 100);
        // DESCRIPTIONS[0]: "下1张打出的光道蛊虫伤害增加 #b"
        // DESCRIPTIONS[1]: "% 。"
        this.description = DESCRIPTIONS[0] + percentage + DESCRIPTIONS[1];
    }
}