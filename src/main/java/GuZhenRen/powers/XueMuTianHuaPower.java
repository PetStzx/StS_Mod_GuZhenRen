package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BufferPower;

public class XueMuTianHuaPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XueMuTianHuaPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private boolean isRemoved = false;
    private int bufferGivenThisTurn = 0; // 用于追踪回合结束时给的缓冲层数

    public XueMuTianHuaPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/XueMuTianHuaPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XueMuTianHuaPower.png");
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    // 回合开始时，检查并收回上一回合给出的缓冲
    @Override
    public void atStartOfTurn() {
        if (this.bufferGivenThisTurn > 0) {
            AbstractPower buffer = this.owner.getPower(BufferPower.POWER_ID);
            if (buffer != null) {
                // 最多收回我们给出的层数
                int amountToRemove = Math.min(buffer.amount, this.bufferGivenThisTurn);
                if (amountToRemove > 0) {
                    this.addToBot(new ReducePowerAction(this.owner, this.owner, BufferPower.POWER_ID, amountToRemove));
                }
            }
            this.bufferGivenThisTurn = 0;
        }
    }

    // 回合结束时给缓冲，并记录层数
    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        if (!this.isRemoved) {
            this.flash();
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new BufferPower(this.owner, this.amount), this.amount));
            this.bufferGivenThisTurn = this.amount;
        }
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (!this.isRemoved && info.type == DamageInfo.DamageType.NORMAL && info.owner == this.owner && target != this.owner) {
            this.flash();
            this.isRemoved = true;
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        }
    }


    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if (!this.isRemoved && damageAmount > 0 && info != null && info.type != DamageInfo.DamageType.HP_LOSS) {
            this.flash();
            this.isRemoved = true;
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        }
    }
}