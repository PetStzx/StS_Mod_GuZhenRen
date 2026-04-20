package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class BuMieXingBiaoPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("BuMieXingBiaoPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BuMieXingBiaoPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/BuMieXingBiaoPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/BuMieXingBiaoPower.png");

        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        this.addToBot(new BuMieXingBiaoAction(this.owner, this));
    }

    public static class BuMieXingBiaoAction extends AbstractGameAction {
        private final AbstractPower power;

        public BuMieXingBiaoAction(AbstractCreature target, AbstractPower power) {
            this.target = target;
            this.power = power;
            this.actionType = ActionType.SPECIAL;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                AbstractPlayer p = AbstractDungeon.player;

                AbstractPower nian = p.getPower(NianPower.POWER_ID);
                if (nian != null) {
                    nian.stackPower(this.power.amount);
                    nian.updateDescription();
                    nian.flashWithoutSound();
                } else {
                    AbstractPower newNian = new NianPower(p, this.power.amount);
                    p.powers.add(newNian);
                    newNian.onInitialApplication();
                    newNian.flashWithoutSound();
                }

                this.power.stackPower(1);
                this.power.updateDescription();
            }

            this.tickDuration();
        }
    }
}