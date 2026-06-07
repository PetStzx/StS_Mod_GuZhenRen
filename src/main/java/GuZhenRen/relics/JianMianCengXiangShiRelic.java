package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.JianMianCengXiangShi;
import GuZhenRen.powers.HaoYouPower;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class JianMianCengXiangShiRelic extends CustomRelic implements ClickableRelic {
    public static final String ID = GuZhenRen.makeID("JianMianCengXiangShiRelic");

    private static final String IMG = GuZhenRen.assetPath("img/relics/JianMianCengXiangShiRelic.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/JianMianCengXiangShiRelic.png");

    private static final int MAX_BATTLES = 5;
    private static final int HAO_YOU_STACKS = 5;

    private boolean isUsedUp = false;

    public JianMianCengXiangShiRelic() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.SPECIAL, LandingSound.MAGICAL);
        this.counter = MAX_BATTLES;
        this.updateDescriptionDynamically();
    }

    @Override
    public String getUpdatedDescription() {
        int displayCount = Math.max(0, this.counter >= 0 ? this.counter : MAX_BATTLES);
        return DESCRIPTIONS[0] + displayCount + DESCRIPTIONS[1] + HAO_YOU_STACKS + DESCRIPTIONS[2];
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
    }

    @Override
    public void atBattleStart() {
        if (this.counter > 0) {
            this.flash();
            AbstractPlayer p = AbstractDungeon.player;

            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    this.addToBot(new ApplyPowerAction(mo, p, new HaoYouPower(mo, HAO_YOU_STACKS), HAO_YOU_STACKS));
                }
            }

            this.counter--;
            this.updateDescriptionDynamically();

            if (this.counter <= 0) {
                returnCardAndRemove();
            }
        }
    }

    @Override
    public void onRightClick() {
        boolean inCombat = AbstractDungeon.isPlayerInDungeon() &&
                AbstractDungeon.getCurrRoom() != null &&
                AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;

        if (inCombat) {
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

        AbstractPlayer p = AbstractDungeon.player;

        boolean inCombat = AbstractDungeon.isPlayerInDungeon() &&
                AbstractDungeon.getCurrRoom() != null &&
                AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;

        if (inCombat) {
            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractCard masterCard = new JianMianCengXiangShi();
                    p.masterDeck.addToTop(masterCard);

                    AbstractCard combatCard = masterCard.makeStatEquivalentCopy();
                    combatCard.uuid = masterCard.uuid;
                    AbstractDungeon.effectList.add(new com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect(combatCard));

                    p.relics.remove(JianMianCengXiangShiRelic.this);
                    p.reorganizeRelics();

                    this.isDone = true;
                }
            });
        } else {
            executeDelayedRemoval();
        }
    }

    @Override
    public void onVictory() {
        if (this.isUsedUp && AbstractDungeon.player.relics.contains(this)) {
            executeDelayedRemoval();
        }
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if ((this.isUsedUp || this.counter == 0) && AbstractDungeon.player.relics.contains(this)) {
            this.isUsedUp = true;
            executeDelayedRemoval();
        }
    }

    private void executeDelayedRemoval() {
        AbstractDungeon.topLevelEffects.add(new AbstractGameEffect() {
            @Override
            public void update() {
                AbstractPlayer p = AbstractDungeon.player;
                if (p.relics.contains(JianMianCengXiangShiRelic.this)) {
                    AbstractCard masterCard = new JianMianCengXiangShi();
                    p.masterDeck.addToTop(masterCard);
                    p.relics.remove(JianMianCengXiangShiRelic.this);
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