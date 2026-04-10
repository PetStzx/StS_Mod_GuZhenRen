package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DuoChongJianYingGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("DuoChongJianYingGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION; // 引入升级后的描述
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/DuoChongJianYingGu.png");

    private static final int COST = 1;
    private static final int MAGIC = 3; // 发 3 张
    private static final int INITIAL_RANK = 4; // 4转

    public DuoChongJianYingGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON, // 白卡
                CardTarget.NONE);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);
        this.baseMagicNumber = this.magicNumber = MAGIC;

        this.cardsToPreview = new JianYing();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractCard jianYing = new JianYing();

        if (this.upgraded) {
            jianYing.upgrade();
        }

        this.addToBot(new MakeTempCardInHandAction(jianYing, this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1);

            AbstractCard upgradedPreview = new JianYing();
            upgradedPreview.upgrade();
            this.cardsToPreview = upgradedPreview;


            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}