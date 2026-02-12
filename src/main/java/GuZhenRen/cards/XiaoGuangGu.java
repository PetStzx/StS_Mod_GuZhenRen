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
    private static final int INITIAL_RANK = 1; // 初始1转

    public XiaoGuangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.BASIC, // 初始牌
                CardTarget.ENEMY); // 目标是敌人

        this.setDao(Dao.GUANG_DAO);
        this.setRank(INITIAL_RANK);

        // baseMagicNumber 用来存储"闪耀"的层数 (1层 = +50%)
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 给予自己"闪耀"
        this.addToBot(new ApplyPowerAction(p, p, new ShanYaoPower(p, this.magicNumber), this.magicNumber));

        // 2. 给予敌人1层虚弱
        this.addToBot(new ApplyPowerAction(m, p, new WeakPower(m, 1, false), 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();

            // 升级后，闪耀变为2层 (+100%)
            this.upgradeMagicNumber(1);

            // 升级后变为 2转
            this.upgradeRank(1);

            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;

            this.initializeDescription();
        }
    }
}