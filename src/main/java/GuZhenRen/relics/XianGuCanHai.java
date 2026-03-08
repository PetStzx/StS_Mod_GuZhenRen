package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class XianGuCanHai extends CustomRelic {
    public static final String ID = GuZhenRen.makeID("XianGuCanHai");
    private static final String IMG = "XianGuCanHai.png";
    private static final String OUTLINE = "XianGuCanHai.png";

    public XianGuCanHai() {
        super(ID,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + IMG)),
                new Texture(GuZhenRen.assetPath("img/relics/outline/" + OUTLINE)),
                RelicTier.SPECIAL,
                LandingSound.MAGICAL);

        this.counter = -1; // 初始状态
    }

    // =========================================================================
    // 此方法专供图鉴、控制台和初始化时调用，永远展示标准的 1 层效果
    // =========================================================================
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + 1 + DESCRIPTIONS[1];
    }

    // =========================================================================
    // 我们自己封装一个方法，用于在游戏流程中动态刷新当前状态的文本
    // =========================================================================
    private void updateDescriptionAndTips() {
        if (this.counter == -2 || this.counter == 0) {
            this.description = DESCRIPTIONS[2]; // "残骸中的道痕已耗尽。"
        } else {
            int displayCount = this.counter > 0 ? this.counter : 1;
            this.description = DESCRIPTIONS[0] + displayCount + DESCRIPTIONS[1];
        }

        // 清空旧提示，强行把新文本塞进去
        this.tips.clear();
        this.tips.add(new com.megacrit.cardcrawl.helpers.PowerTip(this.name, this.description));
        this.initializeTips();
    }

    // =========================================================================
    // 层数与状态管理
    // =========================================================================
    public void addCharge() {
        if (this.counter < 0) {
            this.counter = 0; // 如果之前是隐藏状态，先拉回 0
        }
        this.counter += 1;
        this.grayscale = false; // 点亮遗物
        this.flash();

        updateDescriptionAndTips(); // 动态刷新面板
    }

    public void useCharge() {
        if (this.counter > 0) {
            this.flash();
            this.counter -= 1;

            // 如果刚好耗尽，设为 -2 隐藏数字，并变灰
            if (this.counter == 0) {
                this.counter = -2;
                this.grayscale = true;
            }

            updateDescriptionAndTips(); // 动态刷新面板
        }
    }

    // =========================================================================
    // 当遗物进入玩家遗物栏时触发
    // =========================================================================
    @Override
    public void onEquip() {
        // 如果是刚生成的遗物（层数为 -1），则初始化为 1 层
        if (this.counter == -1) {
            this.counter = 1;
            updateDescriptionAndTips(); // 刷新面板显示出数字 "1"
        }
    }

    // =========================================================================
    // 读档时的状态同步
    // =========================================================================
    @Override
    public void setCounter(int setCounter) {
        this.counter = setCounter;

        if (this.counter == 0 || this.counter <= -2) {
            this.counter = -2;
            this.grayscale = true;
        } else {
            this.grayscale = false;
        }

        updateDescriptionAndTips(); // 读档后也必须手动刷新一次
    }
}