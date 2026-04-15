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
import java.util.HashSet;
import java.util.Set;

public class RenGu extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("RenGu");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/RenGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 1;

    public RenGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
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

    public static class RenGuAction extends AbstractGameAction {
        private int rank;
        private boolean retrieveCard = false;

        public RenGuAction(int rank) {
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_FAST;
            this.rank = rank;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {

                // 【修复1：独立计算并立刻给予剑锋】
                // 把获得剑锋的逻辑提到了最前面，这样无论后面玩家是否跳过选牌，都能稳稳吃到增益
                int jianFeng = 0;
                if (rank == 5 || rank == 6) jianFeng = 1;
                else if (rank == 7 || rank == 8) jianFeng = 2;
                else if (rank == 9) jianFeng = 3;

                if (jianFeng > 0) {
                    AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new JianFengPower(AbstractDungeon.player, jianFeng), jianFeng));
                }

                int choices = 1;
                if (rank >= 2 && rank <= 7) choices = 2;
                if (rank >= 8) choices = 3;

                int poolType = 1;
                if (rank == 3) poolType = 2;
                if (rank >= 4) poolType = 3;

                boolean doUpgrade = (rank >= 6);

                Set<String> playerXianGuIDs = new HashSet<>();
                if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck != null) {
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (c instanceof AbstractGuZhenRenCard && ((AbstractGuZhenRenCard) c).isXianGu()) {
                            playerXianGuIDs.add(c.cardID);
                        }
                    }
                }

                ArrayList<AbstractCard> validCards = new ArrayList<>();
                for (AbstractCard c : CardLibrary.getAllCards()) {
                    if (c instanceof AbstractGuZhenRenCard) {
                        AbstractGuZhenRenCard guCard = (AbstractGuZhenRenCard) c;

                        // 【修复2：使用 tags.contains 绕过拦截器】
                        // 不使用 hasTag()，直接读取底层 tags 数组，确保选出来的永远是“原生剑道牌”
                        if (guCard.tags.contains(GuZhenRenTags.JIAN_DAO) &&
                                !guCard.cardID.equals(RenGu.ID) &&
                                guCard.rank >= 1 && guCard.rank <= 9) {

                            boolean match = false;
                            if (poolType == 1 && c.rarity == CardRarity.COMMON) match = true;
                            if (poolType == 2 && (c.rarity == CardRarity.COMMON || c.rarity == CardRarity.UNCOMMON)) match = true;
                            if (poolType == 3 && (c.rarity == CardRarity.COMMON || c.rarity == CardRarity.UNCOMMON || c.rarity == CardRarity.RARE)) match = true;

                            if (match) {
                                int simulatedRank = Math.min(9, guCard.baseRank + (doUpgrade ? 1 : 0));
                                boolean simulatedXianGu = guCard.hasTag(GuZhenRenTags.XIAN_GU) || simulatedRank >= 6;

                                if (simulatedXianGu && playerXianGuIDs.contains(guCard.cardID)) {
                                    match = false;
                                }
                            }

                            if (match) {
                                validCards.add(c.makeCopy());
                            }
                        }
                    }
                }

                java.util.Collections.shuffle(validCards, new java.util.Random(AbstractDungeon.cardRandomRng.randomLong()));

                CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractCard c : validCards) {
                    if (group.size() >= choices) break;
                    if (doUpgrade) c.upgrade();
                    group.addToTop(c);
                }

                if (group.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                if (choices == 1 || group.size() == 1) {
                    AbstractCard c = group.getTopCard();
                    c.setCostForTurn(0);
                    // 只有一张卡时，直接加入手牌（剑锋已经在前面给过了）
                    AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(c, 1));
                    this.isDone = true;
                    return;
                }

                String msg = RenGu.cardStrings.EXTENDED_DESCRIPTION[9];
                // 打开选卡界面 (第三个参数 true 允许玩家点击跳过)
                AbstractDungeon.cardRewardScreen.customCombatOpen(group.group, msg, true);
                this.tickDuration();
                return;
            }

            if (!this.retrieveCard) {
                // 判断 discoveryCard 是否为空（即玩家是否点了跳过）
                if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                    AbstractCard c = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
                    c.setCostForTurn(0);
                    // 仅执行加入手牌的动作
                    AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(c, 1));
                    AbstractDungeon.cardRewardScreen.discoveryCard = null;
                }
                this.retrieveCard = true;
                this.isDone = true;
            }
            this.tickDuration();
        }
    }

    @Override
    protected void onRankLoaded() {
        calculateStats();
    }
}