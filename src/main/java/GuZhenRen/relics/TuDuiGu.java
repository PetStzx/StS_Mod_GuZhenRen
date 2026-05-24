package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import GuZhenRen.powers.JiTuPower;

public class TuDuiGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("TuDuiGu");
    private static final String IMG = "TuDuiGu.png";
    private static final String OUTLINE = "TuDuiGu.png";

    private static final int JI_TU_AMOUNT = 3;

    public TuDuiGu() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.COMMON,
                LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        this.flash();
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

        this.addToBot(new ApplyPowerAction(
                AbstractDungeon.player,
                AbstractDungeon.player,
                new JiTuPower(AbstractDungeon.player, JI_TU_AMOUNT),
                JI_TU_AMOUNT
        ));
    }
}