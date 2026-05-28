package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class FeiGuDunGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("FeiGuDunGu");
    private static final String IMG = "FeiGuDunGu.png";
    private static final String OUTLINE = "FeiGuDunGu.png";

    private static final int MAX_USES = 3;

    public FeiGuDunGu() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.SPECIAL,
                LandingSound.CLINK);

        this.counter = -1;
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
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onEquip() {
        if (this.counter == -1) {
            this.counter = MAX_USES;
            updateDescriptionAndTips();
        }
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (info != null && info.type != DamageInfo.DamageType.HP_LOSS && damageAmount > 0) {
            if (this.counter > 0) {
                this.flash();

                this.counter -= 1;

                if (this.counter <= 0) {
                    this.counter = -2;
                    this.grayscale = true;
                    this.usedUp();
                }

                updateDescriptionAndTips();
                return 0;
            }
        }
        return damageAmount;
    }

    @Override
    public void setCounter(int setCounter) {
        this.counter = setCounter;

        if (this.counter == 0 || this.counter <= -2) {
            this.counter = -2;
            this.grayscale = true;
            this.usedUp();
        } else {
            this.grayscale = false;
        }
        updateDescriptionAndTips();
    }
}