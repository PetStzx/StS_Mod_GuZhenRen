package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
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

    public ShanYaoPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF; // 正面效果

        // 加载自定义贴图
        // 1. 定义路径
        String pathLarge = GuZhenRen.assetPath("img/powers/ShanYaoPower_p.png"); // 你的 88x88 图
        String pathSmall = GuZhenRen.assetPath("img/powers/ShanYaoPower.png"); // 你的 32x32 图

        // 2. 加载纹理
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);

        // 3. 创建 Region
        // 参数说明: (Texture texture, int x, int y, int width, int height)
        // 这里我们告诉游戏，你的大图是 88x88，小图是 32x32
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    // 【核心逻辑】修改伤害
    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        // 只要卡牌带有 GUANG_DAO 标签，伤害就翻倍
        if (card.hasTag(GuZhenRenTags.GUANG_DAO)) {
            return damage * 2.0F;
        }
        return damage;
    }

    @Override
    public void onUseCard(AbstractCard card, com.megacrit.cardcrawl.actions.utility.UseCardAction action) {
        // 只要使用了带有 GUANG_DAO 标签的 攻击牌，就消耗层数
        if (card.hasTag(GuZhenRenTags.GUANG_DAO) && card.type == AbstractCard.CardType.ATTACK) {
            this.flash();
            this.addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
    }

    // 更新描述
    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}