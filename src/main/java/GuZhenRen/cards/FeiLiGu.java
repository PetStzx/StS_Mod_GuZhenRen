package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FeiLiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FeiLiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("FeiLiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/FeiLiGu.png");

    private static final int COST = 1;
    private static final int BASE_FEILI = 4; // 基础4层
    private static final int UPGRADE_PLUS_FEILI = 2; // 升级+2，共6层（6回合）
    private static final int INITIAL_RANK = 4; // 4转

    public FeiLiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
        this.baseMagicNumber = this.magicNumber = BASE_FEILI;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(m, p, new FeiLiPower(m, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_FEILI);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}