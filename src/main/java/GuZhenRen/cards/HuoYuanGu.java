package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuoYuanGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuoYuanGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuoYuanGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 2; // 2转
    private static final int AMT = 2; // 生成 2 张火势

    public HuoYuanGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON, // 白卡
                CardTarget.NONE);

        this.setDao(Dao.YAN_DAO); // 炎道
        this.setRank(INITIAL_RANK);

        this.baseMagicNumber = this.magicNumber = AMT;

        this.cardsToPreview = new HuoShi();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 触发消耗手牌界面
        this.addToBot(new ExhaustAction(1, false, false, false));

        // 2. 将火势加入手牌
        AbstractCard c = new HuoShi();
        if (this.upgraded) {
            c.upgrade();
        }
        this.addToBot(new MakeTempCardInHandAction(c, this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1); // 2转 -> 3转

            this.cardsToPreview.upgrade();
            this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}