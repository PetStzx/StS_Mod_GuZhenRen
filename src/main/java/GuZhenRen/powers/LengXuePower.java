package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.Map;
import java.util.WeakHashMap;

public class LengXuePower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("LengXuePower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Map<AbstractCreature, Integer> triggerTracker = new WeakHashMap<>();

    public LengXuePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/LengXuePower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/LengXuePower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
            return;
        }

        AbstractPower thisPower = this;

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int currentTurn = AbstractDungeon.actionManager.turn;
                int lastTurn = triggerTracker.getOrDefault(owner, -1);

                if (lastTurn != currentTurn) {
                    triggerTracker.put(owner, currentTurn);

                    thisPower.flash();
                    int damage = Math.max(1, owner.maxHealth / 10);


                    if (thisPower.amount <= 1) {
                        AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(owner, owner, thisPower.ID));
                    } else {
                        AbstractDungeon.actionManager.addToTop(new ReducePowerAction(owner, owner, thisPower.ID, 1));
                    }

                    AbstractDungeon.actionManager.addToTop(new LoseHPAction(owner, owner, damage));
                }

                this.isDone = true;
            }
        });
    }

    @Override
    public void onDeath() {
        triggerTracker.remove(this.owner);
    }

    @Override
    public void onRemove() {
        triggerTracker.remove(this.owner);
    }
}