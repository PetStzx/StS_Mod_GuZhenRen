package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class LiDaoDaoHenPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("LiDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 标记：是否由变化道转化而来
    public boolean isFromBianHua = false;

    public LiDaoDaoHenPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/LiDaoDaoHenPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/LiDaoDaoHenPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    // 力道效果：视为力量增加伤害
    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage + this.amount;
        }
        return damage;
    }

    // 回合结束时，如果是暂时的，变回变化道
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && this.isFromBianHua) {
            this.flash();
            // 1. 移除自己
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            // 2. 变回变化道道痕
            this.addToBot(new ApplyPowerAction(this.owner, this.owner,
                    new BianHuaDaoDaoHenPower(this.owner, this.amount), this.amount));
        }
    }
}