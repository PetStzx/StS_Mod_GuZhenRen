package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;

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
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.actionManager.addToBottom(new JianQiConvertAction(owner, JianQiPower.this));
                this.isDone = true;
            }
        });
    }


    public static class JianQiConvertAction extends AbstractGameAction {
        private final AbstractPower jianQi;

        public JianQiConvertAction(AbstractCreature target, AbstractPower jianQi) {
            this.target = target;
            this.jianQi = jianQi;
            this.actionType = ActionType.SPECIAL;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST && this.target != null && !this.target.isDeadOrEscaped()) {
                int convertAmount = this.jianQi.amount;

                this.target.powers.remove(this.jianQi);

                if (this.target.hasPower(ArtifactPower.POWER_ID)) {
                    CardCrawlGame.sound.play("NULLIFY_SFX");

                    AbstractDungeon.actionManager.addToTop(
                            new com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction(
                                    this.target,
                                    com.megacrit.cardcrawl.actions.common.ApplyPowerAction.TEXT[0]
                            )
                    );

                    this.target.getPower(ArtifactPower.POWER_ID).onSpecificTrigger();
                } else {
                    AbstractPower jianHen = this.target.getPower(JianHenPower.POWER_ID);
                    if (jianHen != null) {
                        jianHen.stackPower(convertAmount);
                        jianHen.updateDescription();
                        jianHen.flashWithoutSound();
                    } else {
                        AbstractPower newJianHen = new JianHenPower(this.target, convertAmount);
                        this.target.powers.add(newJianHen);
                        newJianHen.onInitialApplication();
                        newJianHen.flashWithoutSound();
                    }
                }

                AbstractDungeon.onModifyPower();
            }
            this.tickDuration();
        }
    }
}