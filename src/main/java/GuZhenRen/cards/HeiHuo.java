package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomCard;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class HeiHuo extends CustomCard {
    public static final String ID = GuZhenRen.makeID("HeiHuo");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HeiHuo.png");

    public HeiHuo() {
        super(ID, NAME, IMG_PATH, -2, DESCRIPTION, CardType.CURSE, CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            this.addToBot(new HeiHuoLoseMaxHpAction(3, cardStrings.EXTENDED_DESCRIPTION[0]));
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }

    @Override
    public void triggerOnExhaust() {
        this.addToBot(new HeiHuoLoseMaxHpAction(8, cardStrings.EXTENDED_DESCRIPTION[0]));
    }

    @Override
    public void upgrade() {
    }

    @Override
    public AbstractCard makeCopy() {
        return new HeiHuo();
    }

    public static class HeiHuoLoseMaxHpAction extends AbstractGameAction {
        private int amount;
        private String text;

        public HeiHuoLoseMaxHpAction(int amount, String text) {
            this.amount = amount;
            this.text = text;
            this.actionType = ActionType.DAMAGE;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                AbstractPlayer p = AbstractDungeon.player;

                AbstractDungeon.effectList.add(new BorderFlashEffect(Color.WHITE.cpy()));

                FlashAtkImgEffect darkFire = new FlashAtkImgEffect(p.hb.cX, p.hb.cY, AttackEffect.FIRE);
                ReflectionHacks.setPrivate(darkFire, AbstractGameEffect.class, "color", Color.DARK_GRAY.cpy());
                AbstractDungeon.effectList.add(darkFire);

                AbstractDungeon.effectList.add(new TextAboveCreatureEffect(p.hb.cX - p.animX, p.hb.cY, this.text + this.amount, Color.RED.cpy()));

                if (p.maxHealth <= this.amount) {
                    p.decreaseMaxHealth(p.maxHealth - 1);
                    AbstractDungeon.actionManager.addToTop(new LoseHPAction(p, p, 99999));
                } else {
                    p.decreaseMaxHealth(this.amount);
                }
            }

            this.tickDuration();
        }
    }
}