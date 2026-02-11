package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import GuZhenRen.powers.YanDaoDaoHenPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuoTanGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuoTanGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuoTanGu.png");

    private static final int COST = -2; // -2 表示默认不可打出
    private static final int MAGIC_AMT = 1;
    private static final int UPGRADE_MAGIC_AMT = 1;
    private static final int INITIAL_RANK = 1;

    public HuoTanGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.YAN_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC_AMT;

        // 【关键】按照文本描述，保留“自我保留”属性
        this.selfRetain = true;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

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
        // 由于 canUse 返回 false，这里永远不会被执行
    }

    @Override
    public void triggerOnExhaust() {
        AbstractPlayer p = AbstractDungeon.player;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                // 使用 baseMagicNumber，让 FenShaoPower 内部自动处理加成
                this.addToBot(new ApplyPowerAction(mo, p,
                        new FenShaoPower(mo, this.baseMagicNumber), this.baseMagicNumber));
            }
        }
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