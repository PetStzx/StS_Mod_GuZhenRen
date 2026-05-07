package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ChouHenGuPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("ChouHenGuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private boolean triggeredThisRound = false;

    public ChouHenGuPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/ChouHenGuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/ChouHenGuPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        String coloredName = this.owner.name.replace(" ", " #y");
        this.description = DESCRIPTIONS[0] + coloredName + DESCRIPTIONS[1];
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void atEndOfRound() {
        this.triggeredThisRound = false;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (!this.triggeredThisRound && info.type == DamageInfo.DamageType.NORMAL && info.owner == AbstractDungeon.player && damageAmount > 0) {
            this.flash();

            this.amount += damageAmount;

            this.triggeredThisRound = true;
            this.updateDescription();

            AbstractDungeon.onModifyPower();
        }
        return damageAmount;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && this.amount > 0) {
            damage += this.amount;
        }
        return damage;
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && this.amount > 0) {
            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    ChouHenGuPower.this.amount = 0;
                    ChouHenGuPower.this.updateDescription();

                    AbstractDungeon.onModifyPower();

                    this.isDone = true;
                }
            });
        }
    }
}