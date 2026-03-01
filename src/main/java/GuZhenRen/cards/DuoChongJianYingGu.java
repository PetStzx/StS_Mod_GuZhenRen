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
    private static final int MAGIC = 3; // 数量永远固定为 3 张
    private static final int INITIAL_RANK = 4; // 4转起步

    public DuoChongJianYingGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON, // 白卡
                CardTarget.NONE); // 不指定敌人

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);
        this.baseMagicNumber = this.magicNumber = MAGIC;

        // 设置悬停时预览的衍生牌《剑影》
        this.cardsToPreview = new JianYing();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 实例化一张新的剑影
        AbstractCard jianYing = new JianYing();

        // 2. 如果当前这张《多重剑影蛊》已经升级，就把生成的剑影也升级！
        if (this.upgraded) {
            jianYing.upgrade();
        }

        // 3. 排入动作：将指定数量的剑影加入手牌
        this.addToBot(new MakeTempCardInHandAction(jianYing, this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1); // 4转 -> 5转

            // 将鼠标悬停预览的牌替换为升级后的版本
            AbstractCard upgradedPreview = new JianYing();
            upgradedPreview.upgrade();
            this.cardsToPreview = upgradedPreview;


            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}