package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.OfferingEffect;

public class XinXuePower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XinXuePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public XinXuePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount; // 记录当前的倍率
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/XinXuePower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XinXuePower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);

        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }
    }

    // =========================================================================
    //  核心逻辑：监听生命流失并触发反伤与特效
    // =========================================================================
    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        // 加入判定 !AbstractDungeon.actionManager.turnHasEnded
        // 确保只有在玩家回合内（未点击结束回合）失去生命时，才会触发群体反伤
        if (damageAmount > 0 && !AbstractDungeon.actionManager.turnHasEnded) {
            this.flash();
            // 1. 播放“祭品”特效
            this.addToBot(new VFXAction(new OfferingEffect(), 0.1F));

            int totalDamage = damageAmount * this.amount;

            // 2. 遍历全场敌人，施加扣血
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    this.addToBot(new LoseHPAction(mo, this.owner, totalDamage));
                }
            }
        }
    }
}