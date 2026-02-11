package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture; // 新增导入
import com.badlogic.gdx.graphics.g2d.TextureAtlas; // 新增导入
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster; // 新增导入
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HuoMaoSanZhangPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("HuoMaoSanZhangPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public HuoMaoSanZhangPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1; // -1 表示不可叠加
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/HuoMaoSanZhangPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/HuoMaoSanZhangPower.png");

        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);

        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        // 1. 确保施加者是玩家自己
        if (source == this.owner) {
            // 2. 确保施加的是“焚烧”能力
            if (power instanceof FenShaoPower) {
                // 3. 如果施加的层数小于 3
                if (power.amount < 3) {

                    // --- 步骤 A: 修改 Power 对象的数值 ---
                    // 这对于目标身上【没有】该能力时生效（新建能力）
                    power.amount = 3;

                    // --- 步骤 B: 修改 Action 对象的数值 (核心修复) ---
                    // 这对于目标身上【已有】该能力时生效（叠加层数）
                    // 因为叠加逻辑使用的是 Action 内部的 amount，而不是 power 对象的 amount
                    AbstractGameAction curAction = AbstractDungeon.actionManager.currentAction;

                    // 确保当前正在执行的是 ApplyPowerAction
                    if (curAction instanceof ApplyPowerAction) {
                        // 使用反射强行修改 Action 内部的 protected amount 字段
                        ReflectionHacks.setPrivate(curAction, AbstractGameAction.class, "amount", 3);
                    }

                    this.flash();
                }
            }
        }
    }
}