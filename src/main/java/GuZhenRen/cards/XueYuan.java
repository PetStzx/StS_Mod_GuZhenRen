package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.XueYuanPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XueYuan extends AbstractGuZhenRenCard {
    // ID 修正为 XueYuan
    public static final String ID = GuZhenRen.makeID("XueYuan");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    // 贴图路径修正为 XueYuan.png
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueYuan.png");

    private static final int COST = 1;
    private static final int MAGIC = 2; // 升级前 2 点格挡
    private static final int UPGRADE_PLUS_MAGIC = 1; // 升级后 3 点格挡
    private static final int INITIAL_RANK = 7; // 7转

    public XueYuan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.ENEMY);

        this.setDao(Dao.XUE_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.exhaust = true; // 消耗

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(m, p, new XueYuanPower(m, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1); // 7转 -> 8转
            this.initializeDescription();
        }
    }
}