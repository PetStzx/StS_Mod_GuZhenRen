package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class LongLinGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LongLinGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LongLinGu.png");

    private static final int COST = 1;
    private static final int BASE_BLOCK = 3;         // 每段固定的基础格挡
    private static final int BASE_TIMES = 3;         // 初始3次
    private static final int UPGRADE_PLUS_TIMES = 1; // 升级后+1次（变成4次）
    private static final int INITIAL_RANK = 7;       // 7转

    private boolean showDynamicText = false;

    public LongLinGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.BIAN_HUA_DAO);
        this.setRank(INITIAL_RANK);

        this.baseBlock = this.block = BASE_BLOCK;
        this.baseMagicNumber = this.magicNumber = BASE_TIMES;
        this.baseSecondMagicNumber = this.secondMagicNumber = 0;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int totalTimes = calculateTotalTimes();
        for (int i = 0; i < totalTimes; i++) {
            this.addToBot(new GainBlockAction(p, p, this.block));
        }
    }

    // 计算总格挡次数：基础次数 + 剑锋层数
    private int calculateTotalTimes() {
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) {
            return this.magicNumber;
        }
        AbstractPower jianFeng = AbstractDungeon.player.getPower(GuZhenRen.makeID("JianFengPower"));
        int jianFengCount = (jianFeng != null) ? jianFeng.amount : 0;
        return this.magicNumber + jianFengCount;
    }

    @Override
    protected String constructRawDescription() {
        String s = super.constructRawDescription();
        if (this.showDynamicText) {
            s += cardStrings.EXTENDED_DESCRIPTION[0];
        }
        return s;
    }

    @Override
    public void applyPowers() {
        int totalTimes = calculateTotalTimes();
        if (this.secondMagicNumber != totalTimes) {
            this.secondMagicNumber = totalTimes;
            this.isSecondMagicNumberModified = true;
        }
        this.showDynamicText = true;
        super.applyPowers(); // 这里会自动触发这回合内敏捷对 block 的动态加成计算
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int totalTimes = calculateTotalTimes();
        if (this.secondMagicNumber != totalTimes) {
            this.secondMagicNumber = totalTimes;
            this.isSecondMagicNumberModified = true;
        }
        this.showDynamicText = true;
        super.calculateCardDamage(mo);
    }

    @Override
    public void onMoveToDiscard() {
        this.showDynamicText = false;
        this.initializeDescription();
    }

    @Override
    public void triggerOnExhaust() {
        this.showDynamicText = false;
        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_TIMES); // 3次 -> 4次
            this.upgradeRank(1);                         // 7转 -> 8转
            this.initializeDescription();
        }
    }
}