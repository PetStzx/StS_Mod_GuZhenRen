package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.FenShaoPower;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.OnApplyPowerRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class YanXinGu extends CustomRelic implements OnApplyPowerRelic {
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

    @Override
    public boolean onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (source == AbstractDungeon.player && power instanceof FenShaoPower) {

            // 1. 修改 Power 对象的数值 (用于初次施加)
            power.amount += 1;

            // 2. 修改 Action 对象的数值 (用于叠加层数)
            AbstractGameAction curAction = AbstractDungeon.actionManager.currentAction;
            if (curAction instanceof ApplyPowerAction) {
                int currentAmt = ((ApplyPowerAction) curAction).amount;

                // 【核心修复】这里必须用 AbstractGameAction.class，因为 amount 是在父类定义的
                ReflectionHacks.setPrivate(curAction, AbstractGameAction.class, "amount", currentAmt + 1);
            }

            this.flash();
        }
        return true;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new YanXinGu();
    }
}