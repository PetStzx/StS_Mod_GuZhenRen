package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class YanTongPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("YanTongPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int triggerCount = 0;
    private static final int THRESHOLD = 4; // 阈值

    public YanTongPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount; // 这里 amount 就是焚烧层数 (1 或 2)
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/YanTongPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/YanTongPower.png");

        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);

        if (texLarge != null && texSmall != null) {
            this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
            this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);
        } else {
            this.loadRegion("combust");
        }

        updateDescription();
    }

    @Override
    public void updateDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(DESCRIPTIONS[0]).append(this.amount).append(DESCRIPTIONS[1]);

        int remaining = THRESHOLD - this.triggerCount;
        if (remaining < 0) remaining = 0;

        sb.append(DESCRIPTIONS[2]).append(remaining).append(DESCRIPTIONS[3]);

        this.description = sb.toString();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!card.purgeOnUse && action.target instanceof AbstractMonster) {
            AbstractMonster target = (AbstractMonster) action.target;

            if (!target.isDeadOrEscaped()) {
                this.flash();
                this.triggerCount++;

                this.addToBot(new ApplyPowerAction(target, this.owner,
                        new FenShaoPower(target, this.amount), this.amount, true));

                // 触发4次后给灼伤
                if (this.triggerCount > THRESHOLD) {
                    this.addToBot(new MakeTempCardInHandAction(new Burn(), 1));
                }

                updateDescription();
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }
}