package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
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

    private static final int COST = 1;

    public XingXiuQiPan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new XingXiuQiPanAction());
    }

    // =========================================================================
    //  自定义 Action
    // =========================================================================
    public static class XingXiuQiPanAction extends AbstractGameAction {

        public XingXiuQiPanAction() {
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_MED;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_MED) {
                AbstractPlayer p = AbstractDungeon.player;

                // --- 1. 先把牌捞上手 ---
                // 处理抽牌堆
                moveCardsToHand(p.drawPile, p);
                // 处理弃牌堆
                moveCardsToHand(p.discardPile, p);

                // --- 2. 对手牌中的智道牌减费 ---
                for (AbstractCard c : p.hand.group) {
                    if (isZhiDaoCard(c)) {
                        // 本回合耗能为0
                        c.setCostForTurn(0);
                        c.superFlash();
                    }
                }

                // 刷新手牌布局
                p.hand.refreshHandLayout();

                this.isDone = true;
            }
        }

        /**
         * 将指定牌堆中的智道牌移动到手牌
         */
        private void moveCardsToHand(CardGroup group, AbstractPlayer p) {
            ArrayList<AbstractCard> cardsToMove = new ArrayList<>();

            for (AbstractCard c : group.group) {
                if (isZhiDaoCard(c)) {
                    cardsToMove.add(c);
                }
            }

            for (AbstractCard c : cardsToMove) {
                if (p.hand.size() < 10) {
                    group.removeCard(c);
                    p.hand.addToHand(c);
                    c.lighten(false);
                    c.unhover();
                    c.applyPowers();
                } else {
                    p.createHandIsFullDialog();
                }
            }
        }

        private boolean isZhiDaoCard(AbstractCard c) {
            // 排除自身
            if (c.cardID.equals(XingXiuQiPan.ID)) {
                return false;
            }
            // 使用 Tag 判断
            return c.hasTag(GuZhenRenTags.ZHI_DAO);
        }
    }
}