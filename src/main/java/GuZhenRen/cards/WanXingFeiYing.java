package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.QingPower;
import GuZhenRen.powers.WanXingFeiYingPower;
import GuZhenRen.powers.ZhiDaoDaoHenPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WanXingFeiYing extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("WanXingFeiYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WanXingFeiYing.png");

    private static final int COST = 1;
    private static final int DURATION = 3;
    private static final int NIAN_BASE = 1;

    public WanXingFeiYing() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        // MagicNumber: 持续回合数
        this.baseMagicNumber = this.magicNumber = DURATION;

        // SecondMagicNumber: 获得的念数量
        this.baseSecondMagicNumber = this.secondMagicNumber = NIAN_BASE;

        this.initializeDescription();
    }

    @Override
    public void applyPowers() {
        // 重置数值
        this.secondMagicNumber = this.baseSecondMagicNumber;
        super.applyPowers();

        // 仅在游戏中且玩家存在时计算加成
        if (AbstractDungeon.player != null) {
            int bonus = 0;
            if (AbstractDungeon.player.hasPower(QingPower.POWER_ID)) {
                bonus += AbstractDungeon.player.getPower(QingPower.POWER_ID).amount / 3;
            }
            if (AbstractDungeon.player.hasPower(ZhiDaoDaoHenPower.POWER_ID)) {
                bonus += AbstractDungeon.player.getPower(ZhiDaoDaoHenPower.POWER_ID).amount / 3;
            }

            if (bonus > 0) {
                this.secondMagicNumber += bonus;
                this.isSecondMagicNumberModified = true;
            }
        }

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // Power 内部逻辑会自动计算加成，这里传入基础值
        this.addToBot(new ApplyPowerAction(p, p,
                new WanXingFeiYingPower(p, this.magicNumber, this.baseSecondMagicNumber),
                this.magicNumber));
    }
}