package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import GuZhenRen.powers.HuoMaoSanZhangPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuoMaoSanZhangGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuoMaoSanZhangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuoMaoSanZhangGu.png");

    private static final int COST = 2;
    private static final int INITIAL_RANK = 4;

    public HuoMaoSanZhangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);

        this.setDao(Dao.YAN_DAO);
        this.setRank(INITIAL_RANK);

        this.baseFenShao = this.fenShao = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.upgraded) {
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    this.addToBot(new ApplyPowerAction(mo, p, new FenShaoPower(mo, this.fenShao), this.fenShao));
                }
            }
        }

        if (!p.hasPower(HuoMaoSanZhangPower.POWER_ID)) {
            this.addToBot(new ApplyPowerAction(p, p, new HuoMaoSanZhangPower(p, -1)));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}