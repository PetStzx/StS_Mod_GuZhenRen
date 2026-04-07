package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction; // 【修改】引入加入手牌的Action
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YanZhouGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("YanZhouGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YanZhouGu.png");

    private static final int COST = 1;
    private static final int BLOCK_AMT = 12;
    private static final int UPGRADE_PLUS_BLOCK = 3; // 升级后 11+3=14
    private static final int BURN_AMT = 1;
    private static final int INITIAL_RANK = 2; // 初始2转

    public YanZhouGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON, // 白卡
                CardTarget.SELF);

        this.setDao(Dao.YAN_DAO);

        this.baseBlock = this.block = BLOCK_AMT;

        // 使用 magicNumber 记录灼伤数量，方便在描述中显示
        this.baseMagicNumber = this.magicNumber = BURN_AMT;

        this.setRank(INITIAL_RANK);

        // 设置卡牌预览：灼伤
        this.cardsToPreview = new Burn();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得格挡
        this.addToBot(new GainBlockAction(p, p, this.block));

        // 2. 将1张灼伤加入手牌
        this.addToBot(new MakeTempCardInHandAction(new Burn(), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_PLUS_BLOCK);
            this.upgradeRank(1); // 2转 -> 3转
            this.initializeDescription();
        }
    }
}