package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TianYuanBaoLian extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("TianYuanBaoLian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/TianYuanBaoLian.png");

    private static final int COST = 0;
    private static final int ENERGY_GAIN = 2;
    private static final int UPGRADE_PLUS_ENERGY = 1; // 升级后变 3
    private static final int INITIAL_RANK = 3;

    public TianYuanBaoLian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.NONE);

        this.setDao(Dao.MU_DAO);
        this.setRank(INITIAL_RANK);

        this.baseMagicNumber = this.magicNumber = ENERGY_GAIN;

        updateEnergyDescription();
    }


    private void updateEnergyDescription() {
        StringBuilder energyString = new StringBuilder();

        if (this.magicNumber > 0) {
            for (int i = 0; i < this.magicNumber; i++) {
                energyString.append("[E] ");
            }
        } else {
            energyString.append(cardStrings.EXTENDED_DESCRIPTION[0]);
        }


        this.myBaseDescription = DESCRIPTION.replace("{ENERGY}", energyString.toString().trim());

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.magicNumber > 0) {
            this.addToBot(new GainEnergyAction(this.magicNumber));
        }

        // 衰减机制
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                TianYuanBaoLian.this.baseMagicNumber -= 1;

                if (TianYuanBaoLian.this.baseMagicNumber < 0) {
                    TianYuanBaoLian.this.baseMagicNumber = 0;
                }

                TianYuanBaoLian.this.magicNumber = TianYuanBaoLian.this.baseMagicNumber;
                TianYuanBaoLian.this.isMagicNumberModified = true;
                TianYuanBaoLian.this.updateEnergyDescription();

                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_ENERGY); // 2 -> 3
            this.upgradeRank(1); // 3转 -> 4转
            this.updateEnergyDescription();
        }
    }
}