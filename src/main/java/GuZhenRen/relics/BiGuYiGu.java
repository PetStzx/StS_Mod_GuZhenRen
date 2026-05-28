package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.DoubleTapPower;

public class BiGuYiGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("BiGuYiGu");
    private static final String IMG = "BiGuYiGu.png";
    private static final String OUTLINE = "BiGuYiGu.png";

    public BiGuYiGu() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.SPECIAL,
                LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractPlayer p = AbstractDungeon.player;
        AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(p, this));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DoubleTapPower(p, 1), 1));
    }
}