package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.HuoMaoSanZhangPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuoMaoSanZhangGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuoMaoSanZhangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuoMaoSanZhangGu.png");

    private static final int COST = 3;
    private static final int UPGRADED_COST = 2; // 升级 2 费
    private static final int INITIAL_RANK = 4;

    public HuoMaoSanZhangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);

        this.setDao(Dao.YAN_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!p.hasPower(HuoMaoSanZhangPower.POWER_ID)) {
            this.addToBot(new ApplyPowerAction(p, p, new HuoMaoSanZhangPower(p)));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(UPGRADED_COST);
            this.upgradeRank(1); // 4转 -> 5转
            this.initializeDescription();
        }
    }
}