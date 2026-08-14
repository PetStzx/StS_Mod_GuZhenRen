package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.WeiLaiShen;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.ApotheosisAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

public class WeiLaiShenRelic extends CustomRelic implements ClickableRelic {
    public static final String ID = GuZhenRen.makeID("WeiLaiShenRelic");

    private static final String IMG = GuZhenRen.assetPath("img/relics/WeiLaiShenRelic.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/WeiLaiShenRelic.png");

    private static final int MAX_BATTLES = 3;
    private boolean isUsedUp = false;
    private float checkTimer = 0.5f;
    private boolean isBattleActive = false;

    public WeiLaiShenRelic() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.SPECIAL, LandingSound.MAGICAL);
        this.counter = MAX_BATTLES;
        this.updateDescriptionDynamically();
    }

    private boolean isSafeInCombat() {
        return AbstractDungeon.player != null &&
                AbstractDungeon.isPlayerInDungeon() &&
                AbstractDungeon.currMapNode != null &&
                AbstractDungeon.getCurrRoom() != null &&
                AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    @Override
    public String getUpdatedDescription() {
        int displayCount = Math.max(0, this.counter >= 0 ? this.counter : MAX_BATTLES);
        return DESCRIPTIONS[0] + displayCount + DESCRIPTIONS[1];
    }

    public void updateDescriptionDynamically() {
        this.description = this.getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void setCounter(int setCounter) {
        this.counter = setCounter;
        this.updateDescriptionDynamically();

        if (isSafeInCombat()) {
            this.isBattleActive = true;
        }
    }

    @Override
    public void atBattleStart() {
        if (this.counter > 0) {
            this.isBattleActive = true;
            this.flash();
            this.addToBot(new ApotheosisAction());
        }
    }

    @Override
    public void update() {
        super.update();

        if (AbstractDungeon.player == null || this.isUsedUp) {
            this.stopPulse();
            return;
        }

        if (isSafeInCombat()) {
            this.checkTimer -= Gdx.graphics.getDeltaTime();
            if (this.checkTimer <= 0.0f) {
                this.checkTimer = 0.5f;
                boolean shouldPulse = false;

                if (!AbstractRecipeRelic.usedWeiLaiShenThisCombat) {
                    for (AbstractRelic r : AbstractDungeon.player.relics) {
                        if (r instanceof AbstractRecipeRelic) {
                            AbstractRecipeRelic recipe = (AbstractRecipeRelic) r;
                            if (!recipe.usedUp && recipe.canBeBorrowedByWeiLaiShen()) {
                                shouldPulse = true;
                                break;
                            }
                        }
                    }
                }

                if (shouldPulse) {
                    this.beginLongPulse();
                } else {
                    this.stopPulse();
                }
            }
        } else {
            this.stopPulse();
        }
    }

    @Override
    public void onRightClick() {
        if (isSafeInCombat()) {
            if (AbstractDungeon.isScreenUp || AbstractDungeon.actionManager.turnHasEnded || AbstractDungeon.player.isDead) {
                return;
            }
        }

        this.flash();
        returnCardAndRemove();
    }

    private void returnCardAndRemove() {
        if (this.isUsedUp) return;
        this.isUsedUp = true;
        this.isBattleActive = false;

        AbstractPlayer p = AbstractDungeon.player;

        if (isSafeInCombat()) {
            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractCard masterCard = new WeiLaiShen();
                    p.masterDeck.addToTop(masterCard);

                    AbstractCard combatCard = masterCard.makeStatEquivalentCopy();
                    combatCard.uuid = masterCard.uuid;
                    AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(combatCard));

                    p.relics.remove(WeiLaiShenRelic.this);
                    p.reorganizeRelics();
                    this.isDone = true;
                }
            });
        } else {
            executeDelayedRemoval();
        }
    }

    private void handleCombatEnd() {
        if (this.isBattleActive) {
            this.isBattleActive = false;
            if (!this.isUsedUp && this.counter > 0) {
                this.counter--;
                this.updateDescriptionDynamically();
            }
        }
    }

    @Override
    public void onVictory() {
        handleCombatEnd();

        if ((this.isUsedUp || this.counter <= 0) && AbstractDungeon.player.relics.contains(this)) {
            this.isUsedUp = true;
            executeDelayedRemoval();
        }
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if ((this.isUsedUp || this.counter <= 0) && AbstractDungeon.player.relics.contains(this)) {
            this.isUsedUp = true;
            executeDelayedRemoval();
        }
    }

    private void executeDelayedRemoval() {
        AbstractDungeon.topLevelEffects.add(new AbstractGameEffect() {
            @Override
            public void update() {
                AbstractPlayer p = AbstractDungeon.player;
                if (p.relics.contains(WeiLaiShenRelic.this)) {
                    AbstractCard masterCard = new WeiLaiShen();
                    p.masterDeck.addToTop(masterCard);
                    p.relics.remove(WeiLaiShenRelic.this);
                    p.reorganizeRelics();
                }
                this.isDone = true;
            }
            @Override
            public void render(SpriteBatch sb) {}
            @Override
            public void dispose() {}
        });
    }
}