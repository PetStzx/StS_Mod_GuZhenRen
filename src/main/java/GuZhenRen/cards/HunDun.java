package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HunDun extends CustomCard {
    public static final String ID = GuZhenRen.makeID("HunDun");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HunDun.png");

    public HunDun() {
        super(ID, NAME, IMG_PATH, -2, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
        this.cardsToPreview = new HeiHuo();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            this.addToBot(new HunDunDeathAction());
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }

    @Override
    public void triggerOnExhaust() {
        this.addToBot(new MakeTempCardInHandAction(new HeiHuo(), 3));
    }

    @Override
    public void upgrade() {
    }

    @Override
    public AbstractCard makeCopy() {
        return new HunDun();
    }

    public static class HunDunDeathAction extends AbstractGameAction {
        private boolean isFirstTick = true;

        public HunDunDeathAction() {
            this.actionType = ActionType.DAMAGE;
            this.duration = 1.0F;
        }

        @Override
        public void update() {
            if (this.isFirstTick) {
                AbstractPlayer p = AbstractDungeon.player;
                CardCrawlGame.sound.playV("POWER_INTANGIBLE", 1.5F);
                AbstractDungeon.effectList.add(new com.megacrit.cardcrawl.vfx.BorderFlashEffect(com.badlogic.gdx.graphics.Color.DARK_GRAY.cpy()));
                AbstractDungeon.effectList.add(new com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect(
                        p.hb.cX, p.hb.cY, com.badlogic.gdx.graphics.Color.DARK_GRAY.cpy(),
                        com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect.ShockWaveType.CHAOTIC));
                this.isFirstTick = false;
            }

            this.tickDuration();

            if (this.isDone) {
                AbstractDungeon.actionManager.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 99999));
            }
        }
    }
}