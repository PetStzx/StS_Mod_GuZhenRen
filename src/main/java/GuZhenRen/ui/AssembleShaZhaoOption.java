package GuZhenRen.ui;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

public class AssembleShaZhaoOption extends AbstractCampfireOption {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("AssembleOption"));
    public static final String[] TEXT = uiStrings.TEXT;

    public AssembleShaZhaoOption(boolean active) {
        this.label = TEXT[0];
        this.description = TEXT[1];
        this.img = ImageMaster.loadImage(GuZhenRen.assetPath("img/ui/campfire_shazhao.png"));
        this.usable = true;
    }

    @Override
    public void useOption() {
        AbstractDungeon.effectList.add(new CampfireShaZhaoEffect());
    }
}