package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnPlayerDeathPower;
import com.megacrit.cardcrawl.actions.unique.ExpertiseAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class YongShengPower extends AbstractPower implements OnPlayerDeathPower {
    public static final String POWER_ID = GuZhenRen.makeID("YongShengPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public YongShengPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;

        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/YongShengPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/YongShengPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (damageAmount > 0) {
            this.flash();
            return 0;
        }
        return damageAmount;
    }

    @Override
    public boolean onPlayerDeath(AbstractPlayer p, DamageInfo info) {
        this.flash();
        p.isDead = false;
        p.isDying = false;
        p.halfDead = false;
        p.currentHealth = p.maxHealth;
        p.healthBarUpdatedEvent();
        return false;
    }

    @Override
    public void update(int slot) {
        super.update(slot);

        if (AbstractDungeon.player == null || AbstractDungeon.getCurrRoom() == null) return;
        if (AbstractDungeon.getCurrRoom().phase != com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT) return;

        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (!c.freeToPlayOnce) {
                c.freeToPlayOnce = true;
            }
        }

        if (!AbstractDungeon.actionManager.turnHasEnded && !AbstractDungeon.player.isEndingTurn) {
            if (AbstractDungeon.actionManager.actions.isEmpty() && AbstractDungeon.actionManager.currentAction == null) {
                int maxHandSize = BaseMod.MAX_HAND_SIZE;
                if (AbstractDungeon.player.hand.size() < maxHandSize) {
                    if (!AbstractDungeon.player.drawPile.isEmpty() || !AbstractDungeon.player.discardPile.isEmpty()) {
                        AbstractDungeon.actionManager.addToBottom(new ExpertiseAction(this.owner, maxHandSize));
                    }
                }
            }
        }
    }
}