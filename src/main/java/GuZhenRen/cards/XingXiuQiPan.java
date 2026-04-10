package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class XingXiuQiPan extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("XingXiuQiPan");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XingXiuQiPan.png");

    private static final int COST = 2;

    public XingXiuQiPan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);
        this.initializeDescription();
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if (!canUse) {
            return false;
        }

        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
            if (c.cardID.equals(ID)) {
                this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
                return false;
            }
        }
        return true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new XingXiuQiPanAction());
    }

    public static class XingXiuQiPanAction extends AbstractGameAction {

        public XingXiuQiPanAction() {
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_MED;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_MED) {
                AbstractPlayer p = AbstractDungeon.player;

                for (AbstractCard c : p.hand.group) {
                    if (isZhiDaoCard(c)) {
                        c.setCostForTurn(0);
                        c.superFlash();
                    }
                }

                moveCardsToHandAndReduceCost(p.drawPile, p, false);
                moveCardsToHandAndReduceCost(p.discardPile, p, true);

                p.hand.refreshHandLayout();
                this.isDone = true;
            }
        }

        private void moveCardsToHandAndReduceCost(CardGroup group, AbstractPlayer p, boolean isFromDiscardPile) {
            ArrayList<AbstractCard> cardsToMove = new ArrayList<>();

            for (AbstractCard c : group.group) {
                if (isZhiDaoCard(c)) {
                    cardsToMove.add(c);
                }
            }

            for (AbstractCard c : cardsToMove) {
                c.setCostForTurn(0);

                if (p.hand.size() < BaseMod.MAX_HAND_SIZE) {
                    group.removeCard(c);
                    p.hand.addToHand(c);
                    c.lighten(false);
                    c.unhover();
                } else {
                    p.createHandIsFullDialog();
                    if (!isFromDiscardPile) {
                        group.removeCard(c);
                        p.discardPile.addToTop(c);
                    }
                }
                c.applyPowers();
            }
        }

        private boolean isZhiDaoCard(AbstractCard c) {
            if (c.cardID.equals(XingXiuQiPan.ID)) {
                return false;
            }
            return c.hasTag(GuZhenRenTags.ZHI_DAO);
        }
    }
}