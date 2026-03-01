package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.util.IProbabilityCard;
import GuZhenRen.util.ProbabilityHelper;
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
    private static final int UPGRADE_HP_LOSS = -2;

    public float baseChance = 0.05f;

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

    // 实现 IProbabilityCard 接口
    @Override
    public void increaseBaseChance(float amount) {
        this.baseChance += amount;
        if (this.baseChance > 1.0f) this.baseChance = 1.0f;
        this.initializeDescription(); // 刷新描述文本，使概率变色显示
    }

    @Override
    public float getBaseChance() {
        return this.baseChance;
    }


    // 重写克隆方法，继承强化后的概率
    @Override
    public AbstractGuZhenRenCard makeStatEquivalentCopy() {
        ShiBaiGu c = (ShiBaiGu) super.makeStatEquivalentCopy();
        c.baseChance = this.baseChance;
        return c;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new LoseHPAction(p, p, this.magicNumber));

        float realChance = ProbabilityHelper.getModifiedChance(this, this.baseChance);

        if (AbstractDungeon.cardRandomRng.randomBoolean(realChance)) {
            this.addToBot(new MakeTempCardInHandAction(new ChengGongGu(), 1));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_HP_LOSS);
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