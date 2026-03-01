package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.powers.JianFengPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class RenGu extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("RenGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/RenGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 1;

    public RenGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL, // 本命蛊
                CardTarget.SELF);

        this.setDao(Dao.JIAN_DAO);
        this.maxRank = 9;
        this.exhaust = true;

        this.setRank(INITIAL_RANK);
        calculateStats();
    }

    private void calculateStats() {
        if (cardStrings.EXTENDED_DESCRIPTION != null && cardStrings.EXTENDED_DESCRIPTION.length >= 9) {
            String currentDesc = cardStrings.EXTENDED_DESCRIPTION[this.rank - 1];
            this.myBaseDescription = currentDesc;
            this.initializeDescription();
        }
    }

    @Override
    public void applyPowers() {
        calculateStats();
        super.applyPowers();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateStats();
        this.addToBot(new RenGuAction(this.rank));
    }

    @Override
    public void performUpgradeEffect() {
        calculateStats();
    }


    // ==========================================================
    // 专属动作：完美居中界面的“发现”动作
    // ==========================================================
    public static class RenGuAction extends AbstractGameAction {
        private int rank;
        private boolean retrieveCard = false; // 防止重复获取卡牌的锁

        public RenGuAction(int rank) {
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_FAST;
            this.rank = rank;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                int choices = 1;
                if (rank >= 2 && rank <= 6) choices = 2;
                if (rank >= 7) choices = 3;

                int poolType = 1; // 1:普通
                if (rank == 3) poolType = 2; // 2:普通/罕见
                if (rank >= 4) poolType = 3; // 3:任意

                boolean doUpgrade = (rank >= 5);

                // 1. 遍历卡牌库，过滤符合条件的卡牌
                ArrayList<AbstractCard> validCards = new ArrayList<>();
                for (AbstractCard c : CardLibrary.getAllCards()) {
                    if (c instanceof AbstractGuZhenRenCard) {
                        AbstractGuZhenRenCard guCard = (AbstractGuZhenRenCard) c;

                        if (guCard.hasTag(GuZhenRenTags.JIAN_DAO) &&
                                !guCard.cardID.equals(RenGu.ID) &&
                                guCard.rank >= 1 && guCard.rank <= 9) {

                            boolean match = false;
                            if (poolType == 1 && c.rarity == CardRarity.COMMON) match = true;
                            if (poolType == 2 && (c.rarity == CardRarity.COMMON || c.rarity == CardRarity.UNCOMMON)) match = true;
                            if (poolType == 3 && (c.rarity == CardRarity.COMMON || c.rarity == CardRarity.UNCOMMON || c.rarity == CardRarity.RARE)) match = true;

                            if (match) {
                                validCards.add(c.makeCopy());
                            }
                        }
                    }
                }

                // 2. 打乱牌池
                java.util.Collections.shuffle(validCards, new java.util.Random(AbstractDungeon.cardRandomRng.randomLong()));

                // 3. 抽取指定数量的牌
                CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractCard c : validCards) {
                    if (group.size() >= choices) break;

                    // 如果满足升级条件，直接在选牌界面展示升级后的状态
                    if (doUpgrade) c.upgrade();

                    group.addToTop(c);
                }

                if (group.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                // 4. 如果是 1转(仅1张选项)，直接塞入手牌并上锁设费
                if (choices == 1 || group.size() == 1) {
                    AbstractCard c = group.getTopCard();
                    ((AbstractGuZhenRenCard) c).isRankLocked = true; // 锁定转数免惩罚
                    c.setCostForTurn(0); // 进手牌前再设为0费
                    processSelectedCard(c);
                    this.isDone = true;
                    return;
                }

                // 5. 调用原版“发现”的居中UI奖励界面
                AbstractDungeon.cardRewardScreen.customCombatOpen(group.group, "选择1张加入手牌", true);
                this.tickDuration();
                return;
            }

            // 第二阶段：等待玩家在“发现”界面中选好牌
            if (!this.retrieveCard) {
                if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {

                    AbstractCard c = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();

                    if (c instanceof AbstractGuZhenRenCard) {
                        ((AbstractGuZhenRenCard) c).isRankLocked = true; // 锁定转数免惩罚
                    }

                    // 【核心修改】：在最终加入手牌的这一刻，再将它的耗能设为 0
                    c.setCostForTurn(0);

                    processSelectedCard(c);

                    AbstractDungeon.cardRewardScreen.discoveryCard = null;
                    this.retrieveCard = true;
                    this.isDone = true;
                }
            }
            this.tickDuration();
        }

        private void processSelectedCard(AbstractCard c) {
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(c, 1));

            int jianFeng = 0;
            if (rank == 6 || rank == 7) jianFeng = 1;
            else if (rank == 8) jianFeng = 2;
            else if (rank == 9) jianFeng = 3;

            if (jianFeng > 0) {
                AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new JianFengPower(AbstractDungeon.player, jianFeng), jianFeng));
            }
        }
    }
    @Override
    protected void onRankLoaded() {
        calculateStats();
    }
}