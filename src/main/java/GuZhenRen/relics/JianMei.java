package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.JianFengPower;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class JianMei extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("JianMei");
    private static final String IMG = GuZhenRen.assetPath("img/relics/JianMei.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/JianMei.png");

    public JianMei() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    // 在每场战斗开始时触发
    @Override
    public void atBattleStart() {
        this.flash();
        AbstractPlayer p = AbstractDungeon.player;

        AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(p, this));

        // 给予玩家 1 层剑锋
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new JianFengPower(p, 1), 1));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new JianMei();
    }
}