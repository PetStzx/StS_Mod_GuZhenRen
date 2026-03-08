package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.*;
import GuZhenRen.powers.ShanYaoPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

public class XiaoGuangGu extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("XiaoGuangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XiaoGuangGu.png");

    private static final int COST = 0;
    private static final int INITIAL_RANK = 1;

    public XiaoGuangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.BASIC,
                CardTarget.ENEMY);

        this.setDao(Dao.GUANG_DAO);
        this.setRank(INITIAL_RANK);

        // baseMagicNumber 用来存储显示的百分比 (50%)
        this.baseMagicNumber = this.magicNumber = 50;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int powerStacks = this.magicNumber / 50;

        // 1. 给予自己"闪耀"
        this.addToBot(new ApplyPowerAction(p, p, new ShanYaoPower(p, powerStacks), powerStacks));

        // 2. 给予敌人1层虚弱
        this.addToBot(new ApplyPowerAction(m, p, new WeakPower(m, 1, false), 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();

            // 升级后，增加 50%
            this.upgradeMagicNumber(50);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}