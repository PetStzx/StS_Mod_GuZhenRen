package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class ZiLiGengShengGu extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("ZiLiGengShengGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZiLiGengShengGu.png");

    private static final int COST = 1;
    private static final int BASE_HEAL = 4;
    private static final int INITIAL_RANK = 3;

    public ZiLiGengShengGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.baseMagicNumber = BASE_HEAL;
        this.magicNumber = this.baseMagicNumber;

        this.exhaust = true;
        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
        this.tags.add(CardTags.HEALING);
    }

    @Override
    public void applyPowers() {
        this.magicNumber = this.baseMagicNumber;
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            if (p.hasPower(StrengthPower.POWER_ID)) {
                this.magicNumber += p.getPower(StrengthPower.POWER_ID).amount;
            }

            if (this.magicNumber != this.baseMagicNumber) {
                this.isMagicNumberModified = true;
            }
        }
        super.applyPowers();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new HealAction(p, p, this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(0);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}