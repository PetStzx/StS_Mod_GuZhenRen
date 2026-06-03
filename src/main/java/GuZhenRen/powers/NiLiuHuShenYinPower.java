package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.relics.NiLiuHe;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class NiLiuHuShenYinPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("NiLiuHuShenYinPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public NiLiuHuShenYinPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/NiLiuHuShenYinPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/NiLiuHuShenYinPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        boolean hasTriggered = false;

        if (AbstractDungeon.player.hasRelic(NiLiuHe.ID)) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic.relicId.equals(NiLiuHe.ID)) {
                    NiLiuHe niLiuHeRelic = (NiLiuHe) relic;

                    if (niLiuHeRelic.counter < 9) {
                        niLiuHeRelic.counter += this.amount;

                        if (niLiuHeRelic.counter > 9) {
                            niLiuHeRelic.counter = 9;
                        }

                        niLiuHeRelic.updateDescriptionDynamically();
                        niLiuHeRelic.flash();
                        hasTriggered = true;
                    }
                }
            }
        }

        if (hasTriggered) {
            this.flash();
        }
    }
}