package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianTouShouZuPower;
import GuZhenRen.powers.YiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class LeShanLeShuiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LeShanLeShuiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LeShanLeShuiGu.png");

    private static final int COST = 1;
    private static final int YI_AMT = 12;
    private static final int UPGRADE_YI_AMT = 3;
    private static final int INITIAL_RANK = 6;

    public LeShanLeShuiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        this.baseMagicNumber = this.magicNumber = YI_AMT;

        this.exhaust = true;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得意
        this.addToBot(new ApplyPowerAction(p, p, new YiPower(p, this.magicNumber), this.magicNumber));

        // 2. 施加负面状态：念头受阻
        this.addToBot(new ApplyPowerAction(p, p, new NianTouShouZuPower(p)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_YI_AMT);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}