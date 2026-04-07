package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.util.IProbabilityCard;
import GuZhenRen.util.ProbabilityHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ShiBaiGu extends AbstractGuZhenRenCard implements IProbabilityCard {
    public static final String ID = GuZhenRen.makeID("ShiBaiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ShiBaiGu.png");

    private static final int COST = 1;
    private static final int HP_LOSS = 4;
    private static final int UPGRADE_HP_LOSS = -2; // 升级后减少2点，即失去2点生命

    public float baseChance = 0.01f;

    public ShiBaiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        this.setDao(Dao.LU_DAO);
        this.baseMagicNumber = this.magicNumber = HP_LOSS;
        this.setRank(1);

        this.cardsToPreview = new ChengGongGu();
    }

    // =========================================================================
    // IProbabilityCard 接口实现
    // =========================================================================
    @Override
    public void increaseBaseChance(float amount) {
        this.baseChance += amount;
        if (this.baseChance > 1.0f) this.baseChance = 1.0f;
        this.initializeDescription();
    }

    @Override
    public float getBaseChance() {
        return this.baseChance;
    }

    @Override
    public AbstractGuZhenRenCard makeStatEquivalentCopy() {
        ShiBaiGu c = (ShiBaiGu) super.makeStatEquivalentCopy();
        c.baseChance = this.baseChance;
        return c;
    }

    // =========================================================================
    // 核心逻辑：触发成功后双端销毁
    // =========================================================================
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 每次使用必定失去生命
        this.addToBot(new LoseHPAction(p, p, this.magicNumber));

        // 2. 判定概率是否触发
        if (ProbabilityHelper.rollProbability(this, this.baseChance)) {
            // 【触发成功】：给予成功蛊
            this.addToBot(new MakeTempCardInHandAction(new ChengGongGu(), 1));

            // 局内：直接移出游戏（不会进入弃牌堆，也不会进入消耗堆）
            this.purgeOnUse = true;

            // 局外：通过动作从玩家的真实牌库中找到这盘打出的这张牌并永久移除
            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractCard cardToRemove = null;
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        // 必须通过 uuid 比对，确保删掉的是你局外带进来的本体，而不是复制品
                        if (c.uuid.equals(ShiBaiGu.this.uuid)) {
                            cardToRemove = c;
                            break;
                        }
                    }
                    if (cardToRemove != null) {
                        AbstractDungeon.player.masterDeck.removeCard(cardToRemove);
                    }
                    this.isDone = true;
                }
            });
        }
        // 若未触发，什么都不加，卡牌按常规逻辑进入弃牌堆
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_HP_LOSS); // 扣 4 血 -> 扣 2 血
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    @Override
    protected String constructRawDescription() {
        String baseDesc = super.constructRawDescription();
        if (baseDesc.isEmpty()) return "";

        return baseDesc.replace("{CHANCE}", ProbabilityHelper.getDynamicColorString(this, this.baseChance));
    }
}