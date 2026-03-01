package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class QingNiuLaoLiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("QingNiuLaoLiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/QingNiuLaoLiGu.png");

    private static final int COST = 1;
    private static final int BLOCK = 5;
    private static final int UPGRADE_PLUS_BLOCK = 3;
    private static final int INITIAL_RANK = 2;

    public QingNiuLaoLiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON, // 白卡
                CardTarget.SELF);

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);

        this.baseBlock = BLOCK;
        this.exhaust = true; // 消耗

        this.cardsToPreview = new NiuLiXuYing();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得格挡
        this.addToBot(new GainBlockAction(p, p, this.block));

        // 2. 将预览的卡牌复制一份加入手牌
        AbstractCard c = this.cardsToPreview.makeStatEquivalentCopy();
        this.addToBot(new MakeTempCardInHandAction(c, 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_PLUS_BLOCK); // 5 -> 8
            this.upgradeRank(1); // 2转 -> 3转
            this.cardsToPreview.upgrade();
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}