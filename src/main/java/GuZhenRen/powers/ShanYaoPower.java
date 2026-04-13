package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.SanShiSanTianGuang;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
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

        this.updateDescription();
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        if (card.hasTag(GuZhenRenTags.GUANG_DAO)) {
            float multiplier = 1.0F + (this.amount * MULTIPLIER_PER_STACK);

            AbstractPower daoHen = this.owner.getPower(GuangDaoDaoHenPower.POWER_ID);
            if (daoHen != null) {
                multiplier += (daoHen.amount * GuangDaoDaoHenPower.MULTIPLIER_PER_STACK);
            }

            return damage * multiplier;
        }
        return damage;
    }

    @Override
    public void onUseCard(AbstractCard card, com.megacrit.cardcrawl.actions.utility.UseCardAction action) {
        if (card.hasTag(GuZhenRenTags.GUANG_DAO) && card.type == AbstractCard.CardType.ATTACK) {
            AbstractPower riGuang = this.owner.getPower(RiGuangPower.POWER_ID);

            if (riGuang != null) {
                riGuang.flash();
                this.addToBot(new ReducePowerAction(this.owner, this.owner, riGuang, 1));
            } else {
                this.flash();
                this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            }
        }
    }

    @Override
    public void updateDescription() {
        int percentage = (int)(this.amount * MULTIPLIER_PER_STACK * 100);
        this.description = DESCRIPTIONS[0] + percentage + DESCRIPTIONS[1];
    }

    @Override
    public void onInitialApplication() {
        SanShiSanTianGuang.totalShanYaoGainedThisCombat += this.amount;
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        SanShiSanTianGuang.totalShanYaoGainedThisCombat += stackAmount;
        this.updateDescription();
    }
}