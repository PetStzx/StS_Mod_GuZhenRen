package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;

import java.util.ArrayList;

public class HuoXiNiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("HuoXiNiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public HuoXiNiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/HuoXiNiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/HuoXiNiPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && target == AbstractDungeon.player) {
            this.flash();
            this.addToBot(new HuoXiNiAction(this.amount));
        }
    }

    public static class HuoXiNiAction extends AbstractGameAction {
        private int exhaustAmount;

        public HuoXiNiAction(int amount) {
            this.exhaustAmount = amount;
            this.actionType = ActionType.EXHAUST;
        }

        @Override
        public void update() {
            for (int i = 0; i < this.exhaustAmount; i++) {
                ArrayList<AbstractCard> validCards = new ArrayList<>();
                validCards.addAll(AbstractDungeon.player.drawPile.group);
                validCards.addAll(AbstractDungeon.player.discardPile.group);

                if (validCards.isEmpty()) {
                    break;
                }

                AbstractCard cardToExhaust = validCards.get(AbstractDungeon.cardRandomRng.random(validCards.size() - 1));
                CardGroup sourcePile = AbstractDungeon.player.drawPile.contains(cardToExhaust) ? AbstractDungeon.player.drawPile : AbstractDungeon.player.discardPile;

                sourcePile.removeCard(cardToExhaust);
                AbstractDungeon.player.limbo.addToBottom(cardToExhaust);

                AbstractDungeon.actionManager.addToTop(new VisualExhaustAction(cardToExhaust, sourcePile));
            }
            this.isDone = true;
        }
    }

    public static class VisualExhaustAction extends AbstractGameAction {
        private AbstractCard card;
        private CardGroup sourcePile;
        private boolean isFirstTick = true;

        public VisualExhaustAction(AbstractCard card, CardGroup sourcePile) {
            this.card = card;
            this.sourcePile = sourcePile;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.isFirstTick) {
                this.isFirstTick = false;

                if (this.sourcePile == AbstractDungeon.player.drawPile) {
                    this.card.current_x = CardGroup.DRAW_PILE_X;
                    this.card.current_y = CardGroup.DRAW_PILE_Y;
                } else {
                    this.card.current_x = CardGroup.DISCARD_PILE_X;
                    this.card.current_y = CardGroup.DISCARD_PILE_Y;
                }

                this.card.target_x = Settings.WIDTH / 2.0F;
                this.card.target_y = Settings.HEIGHT / 2.0F;
                this.card.targetAngle = 0.0F;
                this.card.lighten(false);
                this.card.drawScale = 0.12F;
                this.card.targetDrawScale = 0.75F;
            }

            this.tickDuration();

            if (this.isDone) {
                this.card.current_x = this.card.target_x;
                this.card.current_y = this.card.target_y;

                AbstractDungeon.topLevelEffectsQueue.add(new ExhaustCardEffect(this.card));
                AbstractDungeon.player.limbo.moveToExhaustPile(this.card);
            }
        }
    }
}