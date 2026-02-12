package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhuiNian extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ZhuiNian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhuiNian.png");

    private static final int COST = 1;
    private static final int UPGRADE_COST = 0; // 升级后变0费
    private static final int INITIAL_RANK = 6;

    // 念获取倍率 (固定为3)
    private static final int NIAN_MULTIPLIER = 3;
    // 打出次数 (固定为1)
    private static final int PLAY_COUNT = 1;

    public ZhuiNian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE, // 金卡
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        // MagicNumber: 打出次数 (1)
        this.baseMagicNumber = this.magicNumber = PLAY_COUNT;

        // SecondMagicNumber: 念倍率 (3) - 虽然描述写死为3了，但保留变量用于计算逻辑
        this.baseSecondMagicNumber = this.secondMagicNumber = NIAN_MULTIPLIER;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 调用自定义动作
        this.addToBot(new ZhuiNianAction(this.magicNumber, this.secondMagicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            // 【修改】升级只改变费用，不改变打出次数
            this.upgradeBaseCost(UPGRADE_COST);
            this.upgradeRank(1); // 6 -> 7

            // 不需要更新描述了，因为只有费用变绿
            this.initializeDescription();
        }
    }

    // =========================================================================
    //  自定义 Action
    // =========================================================================
    public static class ZhuiNianAction extends AbstractGameAction {
        private final int playCount;
        private final int nianMultiplier;

        public ZhuiNianAction(int playCount, int nianMultiplier) {
            this.playCount = playCount;
            this.nianMultiplier = nianMultiplier;
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            AbstractPlayer p = AbstractDungeon.player;

            // 1. 打开选牌界面
            if (this.duration == Settings.ACTION_DUR_FAST) {
                if (p.discardPile.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                // 只选择 1 张
                AbstractDungeon.gridSelectScreen.open(
                        p.discardPile,
                        1,
                        "选择1张牌打出并消耗",
                        false
                );
                this.tickDuration();
                return;
            }

            // 2. 处理选中结果
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                AbstractDungeon.gridSelectScreen.selectedCards.clear();

                if (c != null) {
                    // A. 从弃牌堆移除，加入 Limbo (虚空)
                    p.discardPile.group.remove(c);
                    AbstractDungeon.getCurrRoom().souls.remove(c);

                    p.limbo.group.add(c);
                    c.current_y = -200.0F * Settings.scale;
                    c.target_x = (float)Settings.WIDTH / 2.0F + 200.0F * Settings.scale;
                    c.target_y = (float)Settings.HEIGHT / 2.0F;
                    c.targetAngle = 0.0F;
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;

                    // B. 设置属性
                    c.freeToPlayOnce = true;
                    c.exhaust = true; // 强制消耗
                    c.applyPowers();

                    // C. 获得念
                    int costToCalc = Math.max(0, c.cost);
                    int nianGain = costToCalc * this.nianMultiplier;

                    if (nianGain > 0) {
                        // 1. 先加打牌动作 (后执行)
                        AbstractDungeon.actionManager.addToTop(new UnlimboAction(c));

                        // 虽然 playCount 总是 1，保留循环结构方便以后改动，也不影响性能
                        for (int i = 0; i < this.playCount; i++) {
                            AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                            AbstractDungeon.actionManager.addToTop(new NewQueueCardAction(c, target, false, true));

                            if (i < this.playCount - 1) {
                                AbstractDungeon.actionManager.addToTop(new WaitAction(0.1F));
                            }
                        }

                        // 2. 后加获念动作 (先执行)
                        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p,
                                new NianPower(p, nianGain), nianGain));

                    } else {
                        // 无念，只打牌
                        AbstractDungeon.actionManager.addToTop(new UnlimboAction(c));
                        for (int i = 0; i < this.playCount; i++) {
                            AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                            AbstractDungeon.actionManager.addToTop(new NewQueueCardAction(c, target, false, true));
                        }
                    }
                }
            }

            this.isDone = true;
        }
    }
}