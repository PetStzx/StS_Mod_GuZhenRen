package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.HuoShi;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class YanXinGu extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("YanXinGu");
    private static final String IMG = GuZhenRen.assetPath("img/relics/YanXinGu.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/YanXinGu.png");

    public YanXinGu() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.UNCOMMON, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // 核心钩子：回合开始时触发
    @Override
    public void atTurnStart() {
        this.flash(); // 遗物闪烁
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

        this.addToBot(new MakeTempCardInHandAction(new HuoShi(), 1));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new YanXinGu();
    }
}