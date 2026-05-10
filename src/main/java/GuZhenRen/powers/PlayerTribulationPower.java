package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.relics.AbstractKongQiao;
import GuZhenRen.tribulations.interfaces.ITribulation;
import GuZhenRen.vfx.TianYiEffect;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class PlayerTribulationPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("PlayerTribulationPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private ITribulation currentTribulation;
    private String drawnTypeName;

    public PlayerTribulationPower(AbstractCreature owner, ITribulation tribulation, String drawnTypeName) {
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;

        this.currentTribulation = tribulation;
        this.drawnTypeName = drawnTypeName;

        this.name = (drawnTypeName != null && !drawnTypeName.isEmpty()) ? drawnTypeName : NAME;

        String pathLarge = GuZhenRen.assetPath("img/powers/PlayerTribulationPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/PlayerTribulationPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, largeTexture.getWidth(), largeTexture.getHeight());
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, smallTexture.getWidth(), smallTexture.getHeight());

        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.currentTribulation != null) {
            String title = this.drawnTypeName;
            int category = this.currentTribulation.getCategory();
            int currentRank = AbstractKongQiao.getCurrentRank();
            int baseDescIndex = (currentRank < 6) ? 6 : 0;

            if (category == 0) {
                this.description = String.format(DESCRIPTIONS[baseDescIndex], title, this.currentTribulation.getDescription());
            } else if (category == 1) {
                this.description = String.format(DESCRIPTIONS[1], title);
            } else if (category == 2) {
                this.description = String.format(DESCRIPTIONS[2], title);
            } else {
                this.description = String.format(DESCRIPTIONS[baseDescIndex], title, this.currentTribulation.getDescription());
            }
        } else {
            this.description = "";
        }
    }

    @Override
    public void onInitialApplication() {
        AbstractDungeon.effectsQueue.add(new TianYiEffect());

        if (this.currentTribulation != null) {
            this.currentTribulation.atPreBattle(this);
        }
    }

    @Override
    public void atStartOfTurn() {
        if (this.currentTribulation != null) {
            this.currentTribulation.atStartOfTurn(this);
        }
    }
}