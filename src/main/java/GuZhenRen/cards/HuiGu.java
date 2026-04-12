package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.AbstractPlayerEnum;
import GuZhenRen.patches.CardColorEnum;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class HuiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuiGu");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuiGu.png");

    private static final int COST = 1;
    private static final int PICK_AMT = 1;
    private static final int UPGRADE_PICK_AMT = 1;
    private static final int INITIAL_RANK = 7;

    public HuiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);
        this.setDao(Dao.ZHI_DAO);
        this.baseMagicNumber = this.magicNumber = PICK_AMT;
        this.setRank(INITIAL_RANK);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new HuiGuAction(this.magicNumber));

        if (p.chosenClass != AbstractPlayerEnum.FANG_YUAN) {
            this.addToBot(new MakeTempCardInHandAction(new Regret(), 1));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PICK_AMT);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    public static class HuiGuAction extends AbstractGameAction {
        private final int amount;

        public HuiGuAction(int amount) {
            this.amount = amount;
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                if (AbstractDungeon.player.exhaustPile.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                // 消耗堆牌数小于等于捞取数量，直接全部捞回
                if (AbstractDungeon.player.exhaustPile.size() <= this.amount) {
                    ArrayList<AbstractCard> cardsToRetrieve = new ArrayList<>(AbstractDungeon.player.exhaustPile.group);
                    for (AbstractCard c : cardsToRetrieve) {
                        retrieveCard(c);
                    }
                    // 补上手牌排版刷新，防止卡牌视觉重叠
                    AbstractDungeon.player.hand.refreshHandLayout();
                    this.isDone = true;
                    return;
                }

                // 正常分支：打开选牌界面
                String msg = String.format(HuiGu.cardStrings.EXTENDED_DESCRIPTION[0], this.amount);
                AbstractDungeon.gridSelectScreen.open(
                        AbstractDungeon.player.exhaustPile,
                        this.amount,
                        msg,
                        false,
                        false,
                        false,
                        false
                );
                this.tickDuration();
                return;
            }

            // 处理选牌界面的返回结果
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    retrieveCard(c);
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
            }

            this.tickDuration();
        }

        // 统一的捞牌与判定处理逻辑
        private void retrieveCard(AbstractCard c) {
            c.unhover();
            c.fadingOut = false;

            c.unfadeOut();
            c.lighten(true);
            c.setAngle(0.0F);

            if (AbstractDungeon.player.exhaustPile.contains(c)) {
                AbstractDungeon.player.exhaustPile.removeCard(c);

                // 1. 先塞悔恨
                if (c.cardID.equals(HuiGu.ID)) {
                    AbstractCard regret = new Regret();
                    if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
                        AbstractDungeon.player.hand.addToHand(regret);
                        regret.flash();
                    } else {
                        AbstractDungeon.player.createHandIsFullDialog();
                        AbstractDungeon.player.discardPile.addToTop(regret);
                    }
                }

                // 2. 再塞目标牌
                if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
                    AbstractDungeon.player.hand.addToHand(c);
                    c.setCostForTurn(0);

                    c.freeToPlayOnce = true;

                    c.flash();
                } else {
                    AbstractDungeon.player.createHandIsFullDialog();
                    AbstractDungeon.player.discardPile.addToTop(c);
                }
            }
            c.applyPowers();
        }
    }
}