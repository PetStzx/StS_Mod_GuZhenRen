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
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class ZiYiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ZiYiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZiYiGu.png");

    private static final int COST = 2;
    private static final int UPGRADE_COST = 1;
    private static final int ENEMY_VULN_AMOUNT = 99; // 敌人99层易伤
    private static final int YI_AMT = 3;             // 获得3层意
    private static final int SELF_VULN_BASE = 2;     // 自己吃2层易伤
    private static final int INITIAL_RANK = 3;       // 3转

    public ZiYiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ALL);

        this.setDao(Dao.ZHI_DAO);

        this.baseMagicNumber = this.magicNumber = YI_AMT;
        this.baseSecondMagicNumber = this.secondMagicNumber = SELF_VULN_BASE;

        this.exhaust = true;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 给予自己易伤
        this.addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, this.secondMagicNumber, false), this.secondMagicNumber));

        // 2. 给予所有敌人 99 层易伤
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                this.addToBot(new ApplyPowerAction(mo, p, new VulnerablePower(mo, ENEMY_VULN_AMOUNT, false), ENEMY_VULN_AMOUNT));
            }
        }

        // 3. 获得意
        this.addToBot(new ApplyPowerAction(p, p, new YiPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();

            // 升级后费用减为 1
            this.upgradeBaseCost(UPGRADE_COST);

            // 升级转数 (3转 -> 4转)
            this.upgradeRank(1);

            this.initializeDescription();
        }
    }
}