package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.LengXuePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class LengXue extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LengXue");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LengXue.png");

    private static final int COST = 1;
    private static final int MAGIC = 3; // 升级前 3 回合
    private static final int UPGRADE_PLUS_MAGIC = 1; // 升级后增加 1 回合 (变为 4)
    private static final int INITIAL_RANK = 6; // 5转

    public LengXue() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.ENEMY);

        this.setDao(Dao.XUE_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.exhaust = true;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(m, p, new LengXuePower(m, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}