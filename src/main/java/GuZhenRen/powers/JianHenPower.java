package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class JianHenPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("JianHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public JianHenPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/JianHenPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/JianHenPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        // 判断：伤害来源是卡牌、卡牌拥有“剑道”标签、正常攻击伤害
        if (card != null && card.hasTag(GuZhenRenTags.JIAN_DAO) && type == DamageInfo.DamageType.NORMAL) {
            return damage + this.amount; // 附加等同于剑痕层数的伤害
        }
        return damage;
    }

    // 在回合结束时移除该状态
    @Override
    public void atEndOfRound() {
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
    }
}