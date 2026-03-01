package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.IProbabilityModifier;
import GuZhenRen.cards.AbstractXuYingCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class QuanLiYiFuPower extends AbstractPower implements IProbabilityModifier {
    public static final String POWER_ID = GuZhenRen.makeID("QuanLiYiFuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public QuanLiYiFuPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1; // -1 表示没有层数的状态
        this.type = PowerType.BUFF;

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

    // =========================================================================
    // 实现接口方法，绝对覆盖全局概率（加入安全检查）
    // =========================================================================
    @Override
    public float getAbsoluteProbabilityOverride(AbstractCard card) {
        // 1. 只有虚影牌才受影响
        if (!(card instanceof AbstractXuYingCard)) {
            return -1.0f;
        }

        // 2. 防御性检查：玩家必须存在且在战斗中
        if (AbstractDungeon.player == null || !AbstractDungeon.isPlayerInDungeon()) {
            return -1.0f;
        }

        // 3. 位置安检：这张虚影必须存在于战斗堆中
        // 绝不影响母卡悬停时生成的百科预览图，也不影响 Loadout 里的字典图。
        boolean inCombatGroup = AbstractDungeon.player.hand.contains(card) ||
                AbstractDungeon.player.drawPile.contains(card) ||
                AbstractDungeon.player.discardPile.contains(card) ||
                AbstractDungeon.player.limbo.contains(card) ||
                AbstractDungeon.player.exhaustPile.contains(card);

        if (inCombatGroup) {
            return 1.0f; // 锁定为 100%
        }

        return -1.0f; // 其他情况（如预览图）不干涉
    }
}