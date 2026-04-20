package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;

public class XingHuoLiaoYuanPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XingHuoLiaoYuanPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static boolean isSpreading = false;

    public XingHuoLiaoYuanPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.DEBUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/XingHuoLiaoYuanPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XingHuoLiaoYuanPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    public void triggerSpread(int spreadAmount) {
        if (spreadAmount <= 0) return;
        this.flash();
        this.addToBot(new XingHuoSpreadAction(this.owner, spreadAmount));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }


    public static class XingHuoSpreadAction extends AbstractGameAction {
        private final int spreadAmount;

        public XingHuoSpreadAction(AbstractCreature source, int amount) {
            this.source = source;
            this.spreadAmount = amount;
            this.actionType = ActionType.SPECIAL;
            this.duration = 0.1F;
        }

        @Override
        public void update() {
            if (this.duration == 0.1F) {
                isSpreading = true;

                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (!mo.isDeadOrEscaped() && mo != this.source) {

                        if (mo.hasPower(ArtifactPower.POWER_ID)) {
                            CardCrawlGame.sound.play("NULLIFY_SFX");

                            AbstractDungeon.actionManager.addToTop(
                                    new com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction(
                                            mo,
                                            com.megacrit.cardcrawl.actions.common.ApplyPowerAction.TEXT[0]
                                    )
                            );

                            mo.getPower(ArtifactPower.POWER_ID).onSpecificTrigger();
                        } else {
                            AbstractPower fenShao = mo.getPower(FenShaoPower.POWER_ID);
                            if (fenShao != null) {
                                fenShao.stackPower(this.spreadAmount);
                                fenShao.updateDescription();
                                fenShao.flashWithoutSound();
                            } else {
                                AbstractPower newFenShao = new FenShaoPower(mo, this.spreadAmount);
                                mo.powers.add(newFenShao);
                                newFenShao.onInitialApplication();
                                newFenShao.flashWithoutSound();
                            }
                        }
                    }
                }

                isSpreading = false;
            }
            this.tickDuration();
        }
    }
}