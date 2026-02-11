package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class YangMangBeiHuoYiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("YangMangBeiHuoYiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int burnAmount; // 每次给予的焚烧层数

    public YangMangBeiHuoYiPower(AbstractCreature owner, int times, int burnAmount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = times; // amount 代表反击的次数 (3次)
        this.burnAmount = burnAmount; // 每次给多少层 (1层)

        this.type = PowerType.BUFF;

        // 虽然原版没有显式设置 isTurnBased，但通常设为 true 比较好管理
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/YangMangBeiHuoYiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/YangMangBeiHuoYiPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);

        if (largeTexture != null && smallTexture != null) {
            this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
            this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);
        } else {
            this.loadRegion("flameBarrier");
        }

        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        // 参考原版：叠加时增加反击次数
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        updateDescription();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        // 逻辑与原版保持一致：非荆棘伤害、非生命流失、来源不是自己
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.type != DamageInfo.DamageType.HP_LOSS && info.owner != this.owner) {

            this.flash();

            // 循环触发反击 (amount次)
            for (int i = 0; i < this.amount; i++) {
                // 【关键修改】 使用 addToTop 确保立即生效
                this.addToTop(new ApplyPowerAction(info.owner, this.owner,
                        new FenShaoPower(info.owner, this.burnAmount), this.burnAmount, true));
            }
        }
        return damageAmount;
    }

    // 这确保了它会在整个敌人回合生效，直到你的回合开始才消失
    @Override
    public void atStartOfTurn() {
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }

    @Override
    public void updateDescription() {
        // 描述：本回合每次受到攻击，给予攻击者 #b1 层 #y焚烧 #b3 次。
        this.description = DESCRIPTIONS[0] + this.burnAmount + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }
}