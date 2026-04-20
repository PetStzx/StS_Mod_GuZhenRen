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

public class ZhuiMingHuoPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("ZhuiMingHuoPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int FEN_SHAO_BASE = 5;

    public ZhuiMingHuoPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/ZhuiMingHuoPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/ZhuiMingHuoPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        if (largeTexture != null && smallTexture != null) {
            this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
            this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);
        } else {
            this.loadRegion("demonForm");
        }

        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        this.updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.flash();

        int totalBurn = FEN_SHAO_BASE * this.amount;

        this.addToBot(new ZhuiMingHuoApplyFenShaoAction(this.owner, totalBurn));
    }

    @Override
    public void updateDescription() {
        int displayAmount = FEN_SHAO_BASE * this.amount;
        this.description = DESCRIPTIONS[0] + displayAmount + DESCRIPTIONS[1];
    }

    public static class ZhuiMingHuoApplyFenShaoAction extends AbstractGameAction {
        private final int burnAmount;

        public ZhuiMingHuoApplyFenShaoAction(AbstractCreature target, int amount) {
            this.target = target;
            this.burnAmount = amount;
            this.actionType = ActionType.SPECIAL;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST && this.target != null && !this.target.isDeadOrEscaped()) {
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
                    AbstractPower fenShao = this.target.getPower(FenShaoPower.POWER_ID);
                    if (fenShao != null) {
                        fenShao.stackPower(this.burnAmount);
                        fenShao.updateDescription();
                        fenShao.flashWithoutSound();
                    } else {
                        AbstractPower newFenShao = new FenShaoPower(this.target, this.burnAmount);
                        this.target.powers.add(newFenShao);
                        newFenShao.onInitialApplication();
                        newFenShao.flashWithoutSound();
                    }
                }
            }
            this.tickDuration();
        }
    }

    public static class ZhuiMingHuoSpreadAction extends AbstractGameAction {
        private final int spreadAmount;

        public ZhuiMingHuoSpreadAction(AbstractCreature target, int amount) {
            this.target = target;
            this.spreadAmount = amount;
            this.actionType = ActionType.SPECIAL;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST && this.target != null && !this.target.isDeadOrEscaped()) {
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
                    AbstractPower zhuiMingHuo = this.target.getPower(ZhuiMingHuoPower.POWER_ID);
                    if (zhuiMingHuo != null) {
                        zhuiMingHuo.stackPower(this.spreadAmount);
                        zhuiMingHuo.updateDescription();
                        zhuiMingHuo.flashWithoutSound();
                    } else {
                        AbstractPower newZhuiMingHuo = new ZhuiMingHuoPower(this.target, this.spreadAmount);
                        this.target.powers.add(newZhuiMingHuo);
                        newZhuiMingHuo.onInitialApplication();
                        newZhuiMingHuo.flashWithoutSound();
                    }
                }
            }
            this.tickDuration();
        }
    }
}