package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class DingLi extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("DingLi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/DingLi.png");

    private static final int COST = 2;
    private static final int BASE_BLOCK = 10;

    private static final int STRENGTH_MULTIPLIER = 3; // 力量的倍数乘区
    private static final int UPGRADE_PLUS_MAGIC = 2; // 3 -> 5

    private static final int INITIAL_RANK = 6; // 6转

    public DingLi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.baseBlock = BASE_BLOCK;
        this.baseMagicNumber = this.magicNumber = STRENGTH_MULTIPLIER;
        this.selfRetain = true;
        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void applyPowers() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            int extraBlock = 0;
            if (p.hasPower(StrengthPower.POWER_ID)) {
                extraBlock = p.getPower(StrengthPower.POWER_ID).amount * this.magicNumber;
            }

            int realBaseBlock = this.baseBlock;
            this.baseBlock += extraBlock;
            super.applyPowers();
            this.baseBlock = realBaseBlock;
            this.isBlockModified = (this.block != this.baseBlock);
        } else {
            super.applyPowers();
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            int extraBlock = 0;
            if (p.hasPower(StrengthPower.POWER_ID)) {
                extraBlock = p.getPower(StrengthPower.POWER_ID).amount * this.magicNumber;
            }

            int realBaseBlock = this.baseBlock;
            this.baseBlock += extraBlock;
            super.calculateCardDamage(mo);
            this.baseBlock = realBaseBlock;
            this.isBlockModified = (this.block != this.baseBlock);
        } else {
            super.calculateCardDamage(mo);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC); // 倍率 3 -> 5
            this.upgradeRank(1); // 6转 -> 7转
            this.initializeDescription();
        }
    }
}