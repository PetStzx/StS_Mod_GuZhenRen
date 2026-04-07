package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.BianHuaDaoDaoHenPower;
import GuZhenRen.powers.ZhuanYiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BianTong extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("BianTong");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/BianTong.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 7;
    private static final int MAGIC = 1;

    public BianTong() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);

        this.setDao(Dao.BIAN_HUA_DAO);
        this.setRank(INITIAL_RANK);

        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int removedCount = 0;

        for (AbstractPower power : p.powers) {
            if (power.type == AbstractPower.PowerType.DEBUFF) {
                this.addToBot(new RemoveSpecificPowerAction(p, p, power));
                this.addToBot(new ZhuanYiPower.TriggerAction());
                removedCount++;
            }
        }

        if (removedCount > 0) {
            int totalDaoHen = removedCount * this.magicNumber;
            this.addToBot(new ApplyPowerAction(p, p, new BianHuaDaoDaoHenPower(p, totalDaoHen), totalDaoHen));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1);

            this.selfRetain = true;
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}