package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import GuZhenRen.powers.QingPower;
import GuZhenRen.powers.YiPower;
import GuZhenRen.powers.ZhiZhangPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhiZhang extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ZhiZhang");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhiZhang.png");

    private static final int COST = 2;
    private static final int INITIAL_RANK = 6;

    public ZhiZhang() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p.hasPower(NianPower.POWER_ID)) {
            this.addToBot(new RemoveSpecificPowerAction(p, p, NianPower.POWER_ID));
        }

        if (!this.upgraded) {
            if (p.hasPower(YiPower.POWER_ID)) {
                this.addToBot(new RemoveSpecificPowerAction(p, p, YiPower.POWER_ID));
            }
            if (p.hasPower(QingPower.POWER_ID)) {
                this.addToBot(new RemoveSpecificPowerAction(p, p, QingPower.POWER_ID));
            }
        }

        this.addToBot(new ApplyPowerAction(p, p, new ZhiZhangPower(p)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1);

            this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;

            this.initializeDescription();
        }
    }
}