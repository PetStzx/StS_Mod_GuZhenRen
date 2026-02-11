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

    private static final int COST = 0;
    private static final int VULN_AMOUNT = 9; // 易伤层数
    private static final int YI_AMT = 3; // 基础意改为3
    private static final int INITIAL_RANK = 3; // 3转

    public ZiYiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ALL);

        this.setDao(Dao.ZHI_DAO);

        // 魔法值控制获得的“意”
        this.baseMagicNumber = this.magicNumber = YI_AMT;

        // 消耗
        this.exhaust = true;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 给予自己 9 层易伤
        this.addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, VULN_AMOUNT, false), VULN_AMOUNT));

        // 2. 给予所有敌人 9 层易伤
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                this.addToBot(new ApplyPowerAction(mo, p, new VulnerablePower(mo, VULN_AMOUNT, false), VULN_AMOUNT));
            }
        }

        // 3. 获得意
        this.addToBot(new ApplyPowerAction(p, p, new YiPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();

            // 1. 添加保留属性
            this.selfRetain = true;

            // 2. 升级转数
            this.upgradeRank(1);

            // 3. 【核心修复】 更新基础描述文本
            // 必须同时更新 rawDescription 和 myBaseDescription
            if (cardStrings.UPGRADE_DESCRIPTION != null) {
                this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
                this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;
            }

            // 4. 重新构建描述
            this.initializeDescription();
        }
    }
}