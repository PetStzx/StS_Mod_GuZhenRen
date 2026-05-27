package GuZhenRen.actions.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class CenterCardDisplayAction extends AbstractGameAction {
    private final AbstractCard displayCard;
    private final AbstractCard sourceCard;
    private final Phase phase;
    private boolean firstFrame = true;

    public CenterCardDisplayAction(AbstractCard displayCard, AbstractCard sourceCard, Phase phase) {
        this.displayCard = displayCard;
        this.sourceCard = sourceCard;
        this.phase = phase;

        if (this.phase == Phase.SETUP) {
            this.actionType = ActionType.WAIT;
            this.duration = Settings.FAST_MODE ? 0.3F : 0.45F;
        }
    }

    @Override
    public void update() {
        if (this.phase == Phase.SETUP) {
            if (this.firstFrame) {
                AbstractDungeon.player.limbo.addToTop(this.displayCard);
                this.displayCard.current_x = Settings.WIDTH / 2.0F;
                this.displayCard.current_y = Settings.HEIGHT / 2.0F;
                this.displayCard.target_x = Settings.WIDTH / 2.0F;
                this.displayCard.target_y = Settings.HEIGHT / 2.0F;
                this.displayCard.drawScale = 0.1F;
                this.displayCard.targetDrawScale = 0.9F;
                this.displayCard.transparency = 0.01F;
                this.displayCard.targetTransparency = 1.0F;

                if (this.sourceCard != null) {
                    this.sourceCard.targetTransparency = 0.0F;
                    this.sourceCard.targetDrawScale = 0.1F;
                }
                this.firstFrame = false;
            }
            this.tickDuration();
        } else if (this.phase == Phase.CLEAR) {
            if (AbstractDungeon.player.limbo.contains(this.displayCard)) {
                AbstractDungeon.player.limbo.removeCard(this.displayCard);
            }
            this.isDone = true;
        }
    }

    public enum Phase {SETUP, CLEAR}
}