package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.LongXingHuBuPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class LongXingHuBuGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LongXingHuBuGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LongXingHuBuGu.png");

    private static final int COST = 0;
    private static final int MAGIC_AMT = 2; // 基础2点
    private static final int UPGRADE_PLUS_MAGIC = 1; // 升级变3点
    private static final int INITIAL_RANK = 4; // 4转

    public LongXingHuBuGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.SELF);

        this.setDao(Dao.LI_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC_AMT;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 给予玩家对应层数的“龙行虎步”状态
        this.addToBot(new ApplyPowerAction(p, p, new LongXingHuBuPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC); // 2 -> 3
            this.upgradeRank(1); // 4 -> 5
            this.initializeDescription();
        }
    }
}