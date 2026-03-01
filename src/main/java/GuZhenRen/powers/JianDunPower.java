package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class JianDunPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("JianDunPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public JianDunPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1; // -1 表示该能力不可叠加，并且在 UI 上不会显示数字
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/JianDunPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/JianDunPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.hasTag(GuZhenRenTags.JIAN_DAO)) {

            int cost = card.costForTurn;

            if (card.cost == -1) {
                cost = card.energyOnUse;
            }
            if (card.freeToPlayOnce) {
                cost = 0;
            }

            // 直接根据耗能抽牌，不再乘以层数
            if (cost > 0) {
                this.flash();
                this.addToBot(new DrawCardAction(this.owner, cost));
            }
        }
    }
}