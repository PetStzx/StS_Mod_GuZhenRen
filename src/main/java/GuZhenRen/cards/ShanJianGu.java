package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.ShanJianGuAction;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ShanJianGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ShanJianGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ShanJianGu.png"); // 记得添加图片

    private static final int COST = 1;
    private static final int MAGIC = 2;          // 基础抽2
    private static final int UPGRADE_MAGIC = 1;  // 升级后抽3
    private static final int INITIAL_RANK = 3;   // 3转起步

    public ShanJianGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.NONE);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);

        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 调用我们的自定义抽牌动作，传入当前的魔法数字 (2或3)
        this.addToBot(new ShanJianGuAction(this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_MAGIC); // 抽 2 -> 抽 3
            this.upgradeRank(1);                    // 转数 3 -> 4转
            this.initializeDescription();
        }
    }
}