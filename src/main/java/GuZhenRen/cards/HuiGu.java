package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.AbstractPlayerEnum; // 【核心导入】导入你的角色枚举类
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction; // 用于生成悔恨
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Regret; // 导入悔恨
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class HuiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
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

        // 防止无限循环：回收消耗堆的牌通常自身需要消耗
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 正常效果：从消耗堆捞牌
        this.addToBot(new HuiGuAction(this.magicNumber));

        // 2.如果当前玩家的角色不是 FANG_YUAN，则添加一张“悔恨”到手牌
        if (p.chosenClass != AbstractPlayerEnum.FANG_YUAN) {
            // 这里不需要提示，默默塞进去即可，增加沉浸感
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

    // =========================================================================
    //  内部 Action 类：处理从消耗堆选牌并改费的逻辑
    // =========================================================================
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
                // 1. 如果消耗堆没牌，直接结束
                if (AbstractDungeon.player.exhaustPile.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                // 2. 如果消耗堆数量 <= 需要选择的数量，直接全部拿回来
                if (AbstractDungeon.player.exhaustPile.size() <= this.amount) {
                    // 创建一个临时列表防止并发修改异常
                    ArrayList<AbstractCard> cardsToRetrieve = new ArrayList<>(AbstractDungeon.player.exhaustPile.group);
                    for (AbstractCard c : cardsToRetrieve) {
                        retrieveCard(c);
                    }
                    this.isDone = true;
                    return;
                }

                // 3. 打开选择界面
                String msg = "选择" + this.amount + "张牌加入手牌";
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

            // 4. 处理玩家的选择结果
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    retrieveCard(c);
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
            }

            this.isDone = true;
        }

        private void retrieveCard(AbstractCard c) {
            // 核心逻辑：从消耗堆移除 -> 加入手牌 -> 改费
            c.unhover();
            c.fadingOut = false; // 防止卡牌正在淡出时被捞回导致透明

            // 只有当卡牌还在消耗堆时才移动（防止极端情况下的重复操作）
            if (AbstractDungeon.player.exhaustPile.contains(c)) {
                AbstractDungeon.player.exhaustPile.removeCard(c);
                AbstractDungeon.player.hand.addToHand(c);

                // 本回合耗能为 0
                c.setCostForTurn(0);

                // 闪烁一下提示
                c.flash();
            }

            // 重置一些状态
            c.applyPowers();
        }
    }
}