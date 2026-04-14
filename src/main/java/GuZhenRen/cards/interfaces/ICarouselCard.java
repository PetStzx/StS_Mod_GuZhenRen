package GuZhenRen.cards.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用于轮播多张关联卡面
 */
public interface ICarouselCard {
    List<AbstractCard> getCarouselCards();

    // 单位 秒
    default float getCarouselInterval() {
        return 2.0f;
    }

    default boolean shouldShow(AbstractCard card) {
        return true;
    }

    default void updateCarousel() {
        if (!(this instanceof AbstractCard)) return;
        AbstractCard card = (AbstractCard) this;

        List<AbstractCard> cards = getCarouselCards();
        if (cards == null || cards.isEmpty()) return;
        List<AbstractCard> filteredCards = cards.stream()
                .filter(this::shouldShow)
                .collect(Collectors.toList());

        if (filteredCards.isEmpty()) {
            card.cardsToPreview = null;
        }

        long ms = (long) (getCarouselInterval() * 1000);
        int i = (int) ((System.currentTimeMillis() / ms) % filteredCards.size());
        card.cardsToPreview = filteredCards.get(i);
    }
}
