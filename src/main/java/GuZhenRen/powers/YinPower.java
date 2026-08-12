package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class YinPower extends AbstractPower implements OnReceivePowerPower {
    public static final String POWER_ID = GuZhenRen.makeID("YinPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public YinPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/YinPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/YinPower.png");
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (GuoPower.isGuoApplying) {
            return damageAmount;
        }

        if (damageAmount > 0 && info.type != DamageInfo.DamageType.HP_LOSS) {
            this.flash();
            this.addToTop(new ApplyPowerAction(this.owner, this.owner, new GuoPower(this.owner, 2, damageAmount)));
            return 0;
        }
        return damageAmount;
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (GuoPower.isGuoApplying) {
            return true;
        }

        boolean isDebuff = power.type == PowerType.DEBUFF;
        boolean isNegativeStat = (power.ID.equals(StrengthPower.POWER_ID) ||
                power.ID.equals(DexterityPower.POWER_ID) ||
                power.ID.equals(FocusPower.POWER_ID)) && power.amount < 0;

        if (isDebuff || isNegativeStat) {
            this.flash();
            this.addToTop(new ApplyPowerAction(target, target, new GuoPower(target, 2, power)));
            return false;
        }
        return true;
    }
}