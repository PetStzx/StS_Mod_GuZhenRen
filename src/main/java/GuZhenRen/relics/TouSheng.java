package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.TouShengAction;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class TouSheng extends CustomRelic implements ClickableRelic {
    public static final String ID = GuZhenRen.makeID("TouSheng");
    private static final String IMG = "TouSheng.png";
    private static final String OUTLINE = "TouSheng.png";

    private static final int MAX_USES = 3;
    private static final int STEAL_AMOUNT = 2;

    public TouSheng() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.RARE,
                LandingSound.MAGICAL);

        this.counter = MAX_USES;
    }

    @Override
    public String getUpdatedDescription() {
        if (this.counter == -2 || this.counter == 0) {
            return DESCRIPTIONS[2];
        } else {
            int displayCount = this.counter > 0 ? this.counter : MAX_USES;
            return DESCRIPTIONS[0] + displayCount + DESCRIPTIONS[1];
        }
    }

    private void updateDescriptionAndTips() {
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new com.megacrit.cardcrawl.helpers.PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onRightClick() {
        if (AbstractDungeon.getCurrRoom() != null &&
                AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
                !AbstractDungeon.actionManager.turnHasEnded &&
                !AbstractDungeon.isScreenUp &&
                this.counter > 0) {

            this.flash();

            AbstractDungeon.actionManager.addToBottom(new TouShengAction(STEAL_AMOUNT));

            this.counter -= 1;
            if (this.counter <= 0) {
                this.counter = -2;
                this.grayscale = true;
            }

            updateDescriptionAndTips();
        }
    }

    @Override
    public void onEquip() {
        if (this.counter == -1) {
            this.counter = MAX_USES;
            updateDescriptionAndTips();
        }
    }

    @Override
    public void setCounter(int setCounter) {
        this.counter = setCounter;
        if (this.counter == 0 || this.counter <= -2) {
            this.counter = -2;
            this.grayscale = true;
        } else {
            this.grayscale = false;
        }
        updateDescriptionAndTips();
    }
}