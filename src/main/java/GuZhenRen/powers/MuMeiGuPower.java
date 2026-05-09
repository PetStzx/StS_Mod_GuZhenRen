package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class MuMeiGuPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("MuMeiGuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public MuMeiGuPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        this.amount = calculateHealAmount();

        String pathLarge = GuZhenRen.assetPath("img/powers/MuMeiGuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/MuMeiGuPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    private int calculateHealAmount() {
        if (this.owner == null) return 0;
        int missingHp = this.owner.maxHealth - this.owner.currentHealth;
        return (int) Math.ceil(missingHp * 0.95f);
    }

    @Override
    public void updateDescription() {
        String yellowName = "#y" + this.owner.name.replace(" ", " #y");

        this.description = DESCRIPTIONS[0] + yellowName + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        int expectedHeal = calculateHealAmount();
        if (this.amount != expectedHeal) {
            this.amount = expectedHeal;
            this.updateDescription();
        }
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void onInitialApplication() {
        int lostHp = this.owner.maxHealth / 2;
        this.owner.maxHealth -= lostHp;

        if (this.owner.currentHealth > this.owner.maxHealth) {
            this.owner.currentHealth = this.owner.maxHealth;
        }

        this.owner.healthBarUpdatedEvent();

        this.amount = calculateHealAmount();
        this.updateDescription();
    }

    @Override
    public void duringTurn() {
        if (this.owner == null || this.owner.isDeadOrEscaped() || this.owner.halfDead) {
            return;
        }

        this.amount = calculateHealAmount();
        if (this.amount > 0) {
            this.flash();
            this.addToBot(new HealAction(this.owner, this.owner, this.amount));
        }
    }
}