package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import java.lang.reflect.Type;

public class LiDaoDaoHen extends CustomRelic implements CustomSavable<Integer> {
    public static final String ID = GuZhenRen.makeID("LiDaoDaoHen");

    private static final String IMG_PATH = "img/relics/LiDaoDaoHen.png";
    private static final String OUTLINE_PATH = "img/relics/outline/LiDaoDaoHen.png";

    public LiDaoDaoHen() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath(IMG_PATH)),
                ImageMaster.loadImage(GuZhenRen.assetPath(OUTLINE_PATH)),
                RelicTier.SPECIAL,
                LandingSound.HEAVY);

        this.counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        if (this.counter > 0) {
            this.flash();
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new StrengthPower(AbstractDungeon.player, this.counter), this.counter));
            this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        }
    }

    @Override
    public Integer onSave() {
        return this.counter;
    }

    @Override
    public void onLoad(Integer savedCounter) {
        this.counter = savedCounter != null ? savedCounter : 0;
    }

    @Override
    public Type savedType() {
        return Integer.class;
    }
}