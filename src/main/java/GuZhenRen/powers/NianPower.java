package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class NianPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = GuZhenRen.makeID("NianPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public NianPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/NianPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/NianPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.amount = amount;

        int bonus = getBonus();
        if (bonus > 0) {
            this.amount += bonus;
        }

        updateDescription();
    }

    private int getBonus() {
        int bonus = 0;
        if (owner == null) return 0;
        if (owner.hasPower(QingPower.POWER_ID)) {
            int qingAmt = owner.getPower(QingPower.POWER_ID).amount;
            // 每3层+1
            bonus += qingAmt / 3;
        }
        if (owner.hasPower(ZhiDaoDaoHenPower.POWER_ID)) {
            int daoHenAmt = owner.getPower(ZhiDaoDaoHenPower.POWER_ID).amount;
            // 每3层+1
            bonus += daoHenAmt / 3;
        }
        return bonus;
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        int bonus = getBonus();
        int totalGain = stackAmount + bonus;

        if (owner.hasPower(ZhiZhangPower.POWER_ID)) {
            this.flash();
            int tempHp = totalGain * 2;
            if (tempHp > 0) {
                this.addToTop(new AddTemporaryHPAction(owner, owner, tempHp));
            }
            return;
        }

        this.amount += totalGain;
        checkThreshold();
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        if (owner.hasPower(ZhiZhangPower.POWER_ID)) {
            this.flash();
            int tempHp = this.amount * 2;
            if (tempHp > 0) {
                this.addToTop(new AddTemporaryHPAction(owner, owner, tempHp));
            }
            this.addToTop(new RemoveSpecificPowerAction(owner, owner, this));
            return;
        }

        checkThreshold();
        updateDescription();
    }

    private void checkThreshold() {
        while (this.amount >= 3) {
            this.amount -= 3;
            triggerEffect();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    private void triggerEffect() {
        this.flash();
        this.addToBot(new DrawCardAction(1));
        this.addToBot(new ApplyPowerAction(owner, owner, new YiPower(owner, 1), 1));
    }

    @Override
    public AbstractPower makeCopy() {
        return new NianPower(owner, amount);
    }

    public static boolean isConverted(AbstractCreature owner) {
        if (owner == null) return false;
        if (owner.hasPower(WanWuDaTongBianPower.POWER_ID)) return true;
        if (owner.hasPower(ZhiZhangPower.POWER_ID)) return true;
        return false;
    }
}