package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.BattleStateManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class RuiYiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("RuiYiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static boolean isActive = false;

    static {
        BattleStateManager.onBattleStart(() -> RuiYiPower.isActive = false);
        BattleStateManager.onPostBattle(() -> RuiYiPower.isActive = false);
    }

    public RuiYiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/RuiYiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/RuiYiPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void onInitialApplication() {
        isActive = true;
    }

    @Override
    public void onRemove() {
        isActive = false;
    }

    @Override
    public void onVictory() {
        isActive = false;
    }
}