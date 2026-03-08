package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.BianHuaDaoDaoHenPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BianTong extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("BianTong");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/BianTong.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 7; // 7转仙蛊

    public BianTong() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER, // 能力牌
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE, // 金卡
                CardTarget.SELF);

        this.setDao(Dao.BIAN_HUA_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int convertedCount = 0;

        // 遍历玩家身上的所有状态
        for (AbstractPower power : p.powers) {
            convertedCount++; // 每一个状态提供 1 层转化基数

            // 如果该状态只有 1 层，或者是无层数状态(-1)，则直接移除
            if (power.amount == -1 || power.amount <= 1) {
                this.addToBot(new RemoveSpecificPowerAction(p, p, power));
            } else {
                // 否则，仅减少 1 层
                this.addToBot(new ReducePowerAction(p, p, power.ID, 1));
            }
        }

        // 如果成功转化了状态，发放同等层数的变化道道痕
        if (convertedCount > 0) {
            this.addToBot(new ApplyPowerAction(p, p, new BianHuaDaoDaoHenPower(p, convertedCount), convertedCount));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1); // 7转 -> 8转
            this.selfRetain = true; // 升级后增加保留
            this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}