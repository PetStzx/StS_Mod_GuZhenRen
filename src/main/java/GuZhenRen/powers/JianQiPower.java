package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class JianQiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("JianQiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public JianQiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/JianQiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/JianQiPower.png");

        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atEndOfRound() {
        this.flash();
        this.addToBot(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
            @Override
            public void update() {
                com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(owner, owner, new JianHenPower(owner, JianQiPower.this.amount), JianQiPower.this.amount)
                );
                com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager.addToBottom(
                        new RemoveSpecificPowerAction(owner, owner, JianQiPower.this)
                );

                this.isDone = true;
            }
        });
    }
}