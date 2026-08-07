package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ZhengChangPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("ZhengChangPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int initialAmount;

    public ZhengChangPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.initialAmount = amount;

        this.type = PowerType.BUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/ZhengChangPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/ZhengChangPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (this.owner == null || this.owner.isDeadOrEscaped() || this.owner.halfDead) {
            return;
        }

        if (!isPlayer) {
            if (this.amount == 1) {
                this.addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        ZhengChangPower.this.flash();

                        for (AbstractPower p : AbstractDungeon.player.powers) {
                            if (p.ID.equals(PlayerTribulationPower.POWER_ID)) continue;
                            if (p.ID.equals(YongShengPower.POWER_ID)) continue;
                            if (p.ID.equals(XianQiaoBengKuiPower.POWER_ID)) continue;

                            AbstractDungeon.actionManager.addToTop(
                                    new RemoveSpecificPowerAction(AbstractDungeon.player, ZhengChangPower.this.owner, p)
                            );
                        }

                        ZhengChangPower.this.amount = ZhengChangPower.this.initialAmount;
                        ZhengChangPower.this.updateDescription();

                        this.isDone = true;
                    }
                });
            } else {
                this.addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
            }
        }
    }
}