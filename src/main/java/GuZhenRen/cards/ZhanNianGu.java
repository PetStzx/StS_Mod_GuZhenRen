package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class ZhanNianGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ZhanNianGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhanNianGu.png");

    private static final int COST = 0;
    private static final int INITIAL_RANK = 2; // 2转

    public ZhanNianGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        this.baseNian = this.nian = 3;

        // secondMagicNumber 用于控制【力量】（基础2点）
        this.baseSecondMagicNumber = this.secondMagicNumber = 2;

        this.setRank(INITIAL_RANK);
    }


    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if (!canUse) {
            return false;
        }

        int attackCount = 0;
        for (AbstractCard c : p.hand.group) {
            if (c.type == CardType.ATTACK) {
                attackCount++;
            }
        }

        if (attackCount < 4) {
            this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
            return false;
        }

        return true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得力量
        this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.secondMagicNumber), this.secondMagicNumber));

        this.addToBot(new ApplyPowerAction(p, p, new NianPower(p, this.nian), this.nian));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeSecondMagicNumber(1);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}