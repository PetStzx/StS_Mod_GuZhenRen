package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.util.ProbabilityHelper;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ShiBaiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ShiBaiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ShiBaiGu.png");

    private static final int COST = 1;
    private static final int HP_LOSS = 4;
    private static final int UPGRADE_HP_LOSS = -2;
    private static final float BASE_CHANCE = 0.05f;

    public ShiBaiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        this.setDao(Dao.LU_DAO);


        this.baseMagicNumber = this.magicNumber = HP_LOSS;

        this.setRank(1);

        // 【核心修复】添加衍生卡预览
        // 这样鼠标悬停在失败蛊上时，旁边会浮现出成功蛊的卡牌详情
        this.cardsToPreview = new ChengGongGu();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new LoseHPAction(p, p, this.magicNumber));

        float realChance = ProbabilityHelper.getModifiedChance(BASE_CHANCE);

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
}