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
        // label = "组并杀招"
        this.label = TEXT[0];

        // description = "将蛊虫组并为杀招。" (无论有没有材料，都显示这个通用描述)
        // 或者你可以保留 active 的判断来改变描述文本，但 usable 必须为 true
        this.description = TEXT[1];

        this.img = ImageMaster.loadImage(GuZhenRen.assetPath("img/ui/campfire_shazhao.png"));

        // 【核心修改】按钮始终可用，允许玩家点击进入查看
        this.usable = true;
    }

    @Override
    public void useOption() {
        // 直接进入特效，逻辑判断交给特效内部处理
        AbstractDungeon.effectList.add(new CampfireShaZhaoEffect());
    }
}