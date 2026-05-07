package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.BattleStateManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class GuoDeQuPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("GuoDeQuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static boolean triggeredThisTurn = false;

    static {
        BattleStateManager.onBattleStart(() -> GuoDeQuPower.triggeredThisTurn = false);
        BattleStateManager.onPostBattle(() -> GuoDeQuPower.triggeredThisTurn = false);
    }

    public GuoDeQuPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/GuoDeQuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/GuoDeQuPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

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
    public void atEndOfRound() {
        triggeredThisTurn = false;
    }

    public void triggerNullify() {
        this.flash();
        CardCrawlGame.sound.play("NULLIFY_SFX");
        triggeredThisTurn = true;
    }

    public static GuoDeQuPower getActiveGuoDeQu() {
        if (triggeredThisTurn) return null;

        if (AbstractDungeon.getMonsters() != null) {
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (!m.isDeadOrEscaped() && m.hasPower(POWER_ID)) {
                    return (GuoDeQuPower) m.getPower(POWER_ID);
                }
            }
        }
        return null;
    }
}