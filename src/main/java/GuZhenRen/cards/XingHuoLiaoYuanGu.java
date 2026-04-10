package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import GuZhenRen.powers.XingHuoLiaoYuanPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XingHuoLiaoYuanGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("XingHuoLiaoYuanGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XingHuoLiaoYuanGu.png");

    private static final int COST = 2;
    private static final int FEN_SHAO_AMT = 1;
    private static final int UPGRADED_TIMES = 3; // 升级后给 3 次
    private static final int INITIAL_RANK = 5;

    public XingHuoLiaoYuanGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.ENEMY);

        this.setDao(Dao.YAN_DAO);

        this.baseFenShao = this.fenShao = FEN_SHAO_AMT;

        this.baseMagicNumber = this.magicNumber = 1;

        this.exhaust = true;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        for (int i = 0; i < this.magicNumber; i++) {
            this.addToBot(new ApplyPowerAction(m, p, new FenShaoPower(m, this.fenShao), this.fenShao));
        }
        this.addToBot(new ApplyPowerAction(m, p, new XingHuoLiaoYuanPower(m,-1)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADED_TIMES - 1);
            this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}