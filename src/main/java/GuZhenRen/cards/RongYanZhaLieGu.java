package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import GuZhenRen.powers.YanDaoDaoHenPower; // 导入
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class RongYanZhaLieGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("RongYanZhaLieGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/RongYanZhaLieGu.png");

    private static final int COST = 2;
    private static final int MAGIC_AMT = 10;
    private static final int UPGRADE_MAGIC_AMT = 3;
    private static final int INITIAL_RANK = 3;

    public RongYanZhaLieGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.YAN_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC_AMT;

        this.setRank(INITIAL_RANK);
    }

    // 【新增】显示逻辑
    @Override
    public void applyPowers() {
        this.magicNumber = this.baseMagicNumber;
        super.applyPowers();

        int bonus = 0;
        if (AbstractDungeon.player.hasPower(YanDaoDaoHenPower.POWER_ID)) {
            bonus = AbstractDungeon.player.getPower(YanDaoDaoHenPower.POWER_ID).amount / 2;
        }

        if (bonus > 0) {
            this.magicNumber += bonus;
            this.isMagicNumberModified = true;
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 【核心修改】使用 baseMagicNumber
        this.addToBot(new ApplyPowerAction(m, p,
                new FenShaoPower(m, this.baseMagicNumber), this.baseMagicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_MAGIC_AMT);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}