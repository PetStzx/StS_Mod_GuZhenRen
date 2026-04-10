package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
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

    private boolean hpLostThisTurn = false;

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

    // 监听生命流失
    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if (damageAmount > 0) {
            this.hpLostThisTurn = true;
        }
    }

    // 回合开始时，重置开关
    @Override
    public void atStartOfTurn() {
        this.hpLostThisTurn = false;
    }

    // 回合结束时，如果本回合掉过血，给予缓冲
    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        if (this.hpLostThisTurn) {
            this.flash();
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new BufferPower(this.owner, this.amount), this.amount));
        }
    }
}