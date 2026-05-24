package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.GuCiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class LeiGuDunGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LeiGuDunGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LeiGuDunGu.png");

    private static final int COST = 1;
    private static final int BLOCK = 6;
    private static final int UPGRADE_PLUS_BLOCK = 3;

    private static final int MAGIC = 1;
    private static final int INITIAL_RANK = 2;

    public LeiGuDunGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        this.setDao(Dao.GU_DAO);
        this.baseBlock = this.block = BLOCK;
        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new LoseHPAction(p, p, this.magicNumber));
        this.addToBot(new GainBlockAction(p, p, this.block));
        this.addToBot(new ApplyPowerAction(p, p, new GuCiPower(p, 1), 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_PLUS_BLOCK);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}