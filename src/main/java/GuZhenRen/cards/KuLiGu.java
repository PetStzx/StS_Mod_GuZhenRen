package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class KuLiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("KuLiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/KuLiGu.png");

    private static final int COST = 1;
    private static final int THRESHOLD = 8;
    private static final int UPGRADE_THRESHOLD = -2;
    private static final int INITIAL_RANK = 4;

    // 【关键】状态开关：控制是否显示括号
    // 默认为 false，保证在图鉴、商店、弃牌堆里不显示
    private boolean showDynamicText = false;

    public KuLiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.LI_DAO);


        this.baseMagicNumber = this.magicNumber = THRESHOLD;
        this.baseSecondMagicNumber = this.secondMagicNumber = 0;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int amount = calculateStrengthAmount();
        if (amount > 0) {
            this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, amount), amount));
        }
    }

    private int calculateStrengthAmount() {
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) return 0;
        int missingHp = AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth;
        if (missingHp < 0) missingHp = 0;
        return missingHp / this.magicNumber;
    }

    @Override
    protected String constructRawDescription() {
        String s = super.constructRawDescription();

        if (this.showDynamicText) {
            // 读取 JSON 中的扩展描述第一项
            s += cardStrings.EXTENDED_DESCRIPTION[0];
        }

        return s;
    }

    @Override
    public void applyPowers() {
        // 1. 计算数值
        int amount = calculateStrengthAmount();
        if (this.secondMagicNumber != amount) {
            this.secondMagicNumber = amount;
            this.isSecondMagicNumberModified = true;
        }

        // 2. 打开开关
        this.showDynamicText = true;

        // 3. 调用父类 applyPowers
        // 父类会调用 initializeDescription -> constructRawDescription
        // 此时 showDynamicText 为 true，所以括号会被加上
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        // 同理
        int amount = calculateStrengthAmount();
        if (this.secondMagicNumber != amount) {
            this.secondMagicNumber = amount;
            this.isSecondMagicNumberModified = true;
        }

        this.showDynamicText = true;
        super.calculateCardDamage(mo);
    }

    @Override
    public void onMoveToDiscard() {
        // 1. 关闭开关
        this.showDynamicText = false;

        // 2. 强制刷新
        // 此时 constructRawDescription 会因为开关关闭而只返回基础描述
        this.initializeDescription();
    }

    // 可选：为了防止被消耗时在消耗堆显示，也加上这个
    @Override
    public void triggerOnExhaust() {
        this.showDynamicText = false;
        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_THRESHOLD);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}