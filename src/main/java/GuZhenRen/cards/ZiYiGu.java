package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.YiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class ZiYiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ZiYiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZiYiGu.png");

    private static final int COST = 1;
    private static final int STR_AMT = 1;

    private static final int MAGIC = 3;
    private static final int UPGRADE_PLUS_MAGIC = 2;

    private static final int YI_AMT = 3;
    private static final int UPGRADE_PLUS_YI = 2;

    private static final int INITIAL_RANK = 3;

    public ZiYiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.ZHI_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.baseSecondMagicNumber = this.secondMagicNumber = YI_AMT;

        this.exhaust = true;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                if (!mo.isDeadOrEscaped()) {
                    this.addToBot(new ApplyPowerAction(mo, p, new StrengthPower(mo, STR_AMT), STR_AMT));
                    this.addToBot(new ApplyPowerAction(mo, p, new VulnerablePower(mo, this.magicNumber, false), this.magicNumber));
                }
            }
        }

        this.addToBot(new ApplyPowerAction(p, p, new YiPower(p, this.secondMagicNumber), this.secondMagicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeSecondMagicNumber(UPGRADE_PLUS_YI);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}