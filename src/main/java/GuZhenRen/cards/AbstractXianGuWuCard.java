package GuZhenRen.cards;

import GuZhenRen.cards.interfaces.ICarouselCard;
import GuZhenRen.patches.GuZhenRenTags;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractXianGuWuCard extends AbstractShaZhaoCard implements ICarouselCard {

    protected final ArrayList<AbstractCard> previewCards = new ArrayList<>();

    public AbstractXianGuWuCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, target);

        this.tags.add(GuZhenRenTags.XIAN_GU_WU);
    }

    @Override
    public void atTurnStart() {
        AbstractPlayer p = AbstractDungeon.player;

        if (p.hand.contains(this)) {
            return;
        }

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (p.hand.size() < BaseMod.MAX_HAND_SIZE) {
                    if (p.drawPile.contains(AbstractXianGuWuCard.this)) {
                        p.drawPile.removeCard(AbstractXianGuWuCard.this);
                        moveToHand();
                    } else if (p.discardPile.contains(AbstractXianGuWuCard.this)) {
                        p.discardPile.removeCard(AbstractXianGuWuCard.this);
                        moveToHand();
                    }
                } else {
                    p.createHandIsFullDialog();
                }
                this.isDone = true;
            }

            private void moveToHand() {
                p.hand.addToHand(AbstractXianGuWuCard.this);
                AbstractXianGuWuCard.this.unhover();
                AbstractXianGuWuCard.this.setAngle(0.0F);
                AbstractXianGuWuCard.this.lighten(false);
                AbstractXianGuWuCard.this.drawScale = 0.12F;
                AbstractXianGuWuCard.this.targetDrawScale = 0.75F;
                AbstractXianGuWuCard.this.applyPowers();
                p.hand.refreshHandLayout();
            }
        });
    }

    @Override
    public void update() {
        super.update();
        this.updateCarousel();
    }

    @Override
    public List<AbstractCard> getCarouselCards() {
        return this.previewCards;
    }

    @Override
    public boolean shouldShow(AbstractCard card) {
        return true;
    }
}