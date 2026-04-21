package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractXuYingCard;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class LiQiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("LiQiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int currentPhantomBonus = 0;
    private boolean isRemoved = false;

    public LiQiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/LiQiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/LiQiPower.png");

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

    private int countPhantomsInHand() {
        int count = 0;
        if (AbstractDungeon.player != null && AbstractDungeon.player.hand != null) {
            int handSize = AbstractDungeon.player.hand.group.size();
            for (int i = 0; i < handSize; i++) {
                AbstractCard c = AbstractDungeon.player.hand.group.get(i);
                if (c instanceof AbstractXuYingCard) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void update(int slot) {
        super.update(slot);

        if (this.isRemoved) {
            return;
        }

        if (AbstractDungeon.player != null) {
            int actualCount = countPhantomsInHand();

            if (actualCount != this.currentPhantomBonus) {
                int diff = actualCount - this.currentPhantomBonus;
                BaseMod.MAX_HAND_SIZE += diff;
                this.currentPhantomBonus = actualCount;
            }
        }
    }

    private void resetHandSizeLimit() {
        if (this.isRemoved) return;
        this.isRemoved = true;

        if (this.currentPhantomBonus > 0) {
            BaseMod.MAX_HAND_SIZE -= this.currentPhantomBonus;
            this.currentPhantomBonus = 0;
        }
    }

    @Override
    public void onRemove() {
        resetHandSizeLimit();
    }

    @Override
    public void onVictory() {
        resetHandSizeLimit();
    }

    @Override
    public void onDeath() {
        resetHandSizeLimit();
    }


    @Override
    public void atStartOfTurnPostDraw() {
        this.flash();
        this.addToBot(new TriggerRoundAction(this.amount));
    }

    private class TriggerRoundAction extends AbstractGameAction {
        private int roundsLeft;

        public TriggerRoundAction(int roundsLeft) {
            this.roundsLeft = roundsLeft;
        }

        @Override
        public void update() {
            if (AbstractDungeon.player != null && this.roundsLeft > 0) {
                for (AbstractCard c : AbstractDungeon.player.hand.group) {
                    if (c instanceof AbstractXuYingCard) {
                        AbstractXuYingCard phantomCard = (AbstractXuYingCard) c;
                        AbstractMonster randomTarget = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                        if (randomTarget != null) {
                            phantomCard.queuePhantomAnimationAndEffect(randomTarget);
                        }
                    }
                }
                if (this.roundsLeft > 1) {
                    this.addToBot(new TriggerRoundAction(this.roundsLeft - 1));
                }
            }
            this.isDone = true;
        }
    }
}