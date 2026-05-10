package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DingKongPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("DingKongPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int currentPenalty = 0;
    private boolean isRemoved = false;

    public DingKongPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/DingKongPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/DingKongPower.png");
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
    public void update(int slot) {
        super.update(slot);

        if (this.isRemoved || AbstractDungeon.getMonsters() == null) {
            return;
        }

        boolean amIFirst = false;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped() && m.hasPower(POWER_ID)) {
                if (m == this.owner) {
                    amIFirst = true;
                }
                break;
            }
        }

        int targetPenalty = amIFirst ? 5 : 0;

        if (targetPenalty != this.currentPenalty) {
            int diff = targetPenalty - this.currentPenalty;
            BaseMod.MAX_HAND_SIZE -= diff;
            this.currentPenalty = targetPenalty;
        }
    }

    private void resetHandSizeLimit() {
        if (this.isRemoved) return;
        this.isRemoved = true;

        if (this.currentPenalty > 0) {
            BaseMod.MAX_HAND_SIZE += this.currentPenalty;
            this.currentPenalty = 0;
        }
    }

    @Override
    public void onRemove() {
        resetHandSizeLimit();
    }

    @Override
    public void onVictory() {
        resetHandSizeLimit();
    }

    @Override
    public void onDeath() {
        resetHandSizeLimit();
    }
}