package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.*;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YuPiGu extends AbstractGuZhenRenCard {

    // 1. 设置ID，格式为 "ModID:CardName"
    public static final String ID = GuZhenRen.makeID("YuPiGu");

    // 2. 读取本地化文本
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    // 3. 图片路径
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YuPiGu.png");

    // 4. 基础数值
    private static final int COST = 1;
    private static final int BLOCK_AMT = 5;
    private static final int UPGRADE_PLUS_BLOCK = 3; // 升级增加3点格挡
    private static final int INITIAL_RANK = 1;       // 初始1转

    public YuPiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,              // 类型：技能
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.BASIC,            // 稀有度：基础（初始牌）
                CardTarget.SELF);            // 目标：自己

        this.baseBlock = BLOCK_AMT;

        this.setDao(Dao.JIN_DAO);


        this.setRank(INITIAL_RANK);
        this.tags.add(CardTags.STARTER_DEFEND);
        // 设置初始转数：1转
        // 父类会自动在描述前添加 "guzhenren:一转 NL"
        this.setRank(INITIAL_RANK);

        // 添加基础防御标签（这有助于某些遗物或事件识别这是基础防御牌）
        this.tags.add(CardTags.STARTER_DEFEND);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得格挡动作
        this.addToBot(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_PLUS_BLOCK);

            // 升级后变为 2转
            this.setRank(2);

            this.initializeDescription();
        }
    }
}