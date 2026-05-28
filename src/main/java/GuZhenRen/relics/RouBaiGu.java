package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class RouBaiGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("RouBaiGu");
    private static final String IMG = "RouBaiGu.png";
    private static final String OUTLINE = "RouBaiGu.png";

    private static final int HEAL_AMT = 4;

    private boolean hasLostHpThisCombat = false;

    public RouBaiGu() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.SPECIAL,
                LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + HEAL_AMT + DESCRIPTIONS[1];
    }

    @Override
    public void atBattleStart() {
        this.hasLostHpThisCombat = false;
    }

    @Override
    public void onLoseHp(int damageAmount) {
        if (damageAmount > 0) {
            this.hasLostHpThisCombat = true;
        }
    }

    @Override
    public void onVictory() {
        if (this.hasLostHpThisCombat && AbstractDungeon.player.currentHealth > 0) {
            this.flash();
            AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            AbstractDungeon.player.heal(HEAL_AMT, true);
        }

        this.hasLostHpThisCombat = false;
    }
}