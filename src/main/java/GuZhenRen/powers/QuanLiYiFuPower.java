package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class QuanLiYiFuPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("QuanLiYiFuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public QuanLiYiFuPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1; // -1 表示这是一个没有层数的状态
        this.type = PowerType.BUFF;

        // 加载自定义图标
        String pathLarge = GuZhenRen.assetPath("img/powers/QuanLiYiFuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/QuanLiYiFuPower.png");

        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);

        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    /**
     * 核心效果：让攻击牌享受双倍力量（和力道）
     */
    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        if (type == DamageInfo.DamageType.NORMAL && card.type == AbstractCard.CardType.ATTACK) {
            float totalBonus = 0;

            // 1. 获取 力量
            if (this.owner.hasPower(StrengthPower.POWER_ID)) {
                totalBonus += this.owner.getPower(StrengthPower.POWER_ID).amount;
            }

            // 2. 获取 力道道痕 (视为力量)
            // 【新增逻辑】
            if (this.owner.hasPower(LiDaoDaoHenPower.POWER_ID)) {
                totalBonus += this.owner.getPower(LiDaoDaoHenPower.POWER_ID).amount;
            }

            // 3. 将总加成再加一遍 (实现翻倍)
            return damage + totalBonus;
        }
        return damage;
    }
}