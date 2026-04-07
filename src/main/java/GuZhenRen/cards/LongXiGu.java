package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import GuZhenRen.powers.JianHenPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class LongXiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LongXiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LongXiGu.png");

    private static final int COST = 1;
    private static final int MAGIC = 6;
    private static final int UPGRADE_PLUS_MAGIC = 2;
    private static final int INITIAL_RANK = 6;

    public LongXiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.BIAN_HUA_DAO);
        this.setRank(INITIAL_RANK);
        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(m, p, new FenShaoPower(m, this.magicNumber), this.magicNumber, true));

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower fenShao = m.getPower(FenShaoPower.POWER_ID);
                if (fenShao != null && fenShao.amount > 0) {
                    this.addToTop(new ApplyPowerAction(m, p, new JianHenPower(m, fenShao.amount), fenShao.amount, true));
                }
                this.isDone = true;
            }
        });
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