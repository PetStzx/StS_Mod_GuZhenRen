package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HuoMaoSanZhangPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("HuoMaoSanZhangPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public HuoMaoSanZhangPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
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
        // 1. 施加者是玩家自己
        if (source == this.owner) {
            // 2. 施加的是“焚烧”
            if (power instanceof FenShaoPower) {

                AbstractGameAction curAction = AbstractDungeon.actionManager.currentAction;
                // 当前正在执行的是 ApplyPowerAction
                if (curAction instanceof ApplyPowerAction) {

                    int currentAmt = curAction.amount;
                    // 3. 如果原本要施加的层数大于0（排除清理buff的情况），且小于3
                    if (currentAmt > 0 && currentAmt < 3) {

                        // 修改 Power 对象的数值（针对初次施加，目标身上还没有该能力时生效）
                        power.amount = 3;

                        // 修改 Action 对象的数值（针对层数叠加）
                        ReflectionHacks.setPrivate(curAction, AbstractGameAction.class, "amount", 3);

                        this.flash();
                    }
                }
            }
        }
    }
}