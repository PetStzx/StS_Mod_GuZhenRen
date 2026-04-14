package GuZhenRen.cards;

import GuZhenRen.cards.interfaces.ICarouselCard;
import GuZhenRen.patches.GuZhenRenTags;
import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
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

    public static void returnAllToHand() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return;

        moveCardsToHand(p.drawPile, p);
        moveCardsToHand(p.discardPile, p);
        moveCardsToHand(p.exhaustPile, p);
    }

    private static void moveCardsToHand(CardGroup group, AbstractPlayer p) {
        ArrayList<AbstractCard> cardsToMove = new ArrayList<>();

        for (AbstractCard c : group.group) {
            if (c instanceof AbstractXianGuWuCard) {
                cardsToMove.add(c);
            }
        }

        for (AbstractCard c : cardsToMove) {
            if (p.hand.size() < BaseMod.MAX_HAND_SIZE) {
                group.removeCard(c);
                p.hand.addToHand(c);

                if (group == p.exhaustPile) {
                    c.unfadeOut();
                }

                c.unhover();
                c.setAngle(0.0F);
                c.lighten(false);
                c.drawScale = 0.12F;
                c.targetDrawScale = 0.75F;
                c.applyPowers();
            } else {
                p.createHandIsFullDialog();
            }
        }

        if (!cardsToMove.isEmpty()) {
            p.hand.refreshHandLayout();
        }
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