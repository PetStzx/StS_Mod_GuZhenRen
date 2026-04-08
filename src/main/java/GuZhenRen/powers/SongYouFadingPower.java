package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;

public class SongYouFadingPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("SongYouFadingPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Fading");
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SongYouFadingPower(AbstractCreature owner, int turns) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = turns;

        this.updateDescription();
        this.loadRegion("fading");
    }

    @Override
    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }
    }

    @Override
    public void duringTurn() {
        boolean isHalfDead = false;
        if (this.owner instanceof AbstractMonster) {
            isHalfDead = ((AbstractMonster) this.owner).halfDead;
        }

        if (this.amount == 1 && !this.owner.isDying && !isHalfDead) {
            this.addToBot(new VFXAction(new ExplosionSmallEffect(this.owner.hb.cX, this.owner.hb.cY), 0.1F));

            this.addToBot(new InstantKillAction(this.owner));
        } else {
            this.addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
            this.updateDescription();
        }
    }
}