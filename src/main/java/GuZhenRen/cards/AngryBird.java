package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.FenShaoPower;
import GuZhenRen.powers.YanDaoDaoHenPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AngryBird extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("AngryBird");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/AngryBird.png");

    private static final int COST = 2;
    private static final int FEN_SHAO_AMT = 20;

    public AngryBird() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ENEMY);

        this.baseMagicNumber = this.magicNumber = FEN_SHAO_AMT;

        this.setDao(Dao.YAN_DAO);

        this.initializeDescription();
    }

    // =========================================================================
    //  【显示逻辑】 只负责让卡面数字变绿，给玩家看
    // =========================================================================
    @Override
    public void applyPowers() {
        // 1. 重置
        this.magicNumber = this.baseMagicNumber;
        super.applyPowers();

        // 2. 计算加成
        int bonus = 0;
        if (AbstractDungeon.player.hasPower(YanDaoDaoHenPower.POWER_ID)) {
            bonus = AbstractDungeon.player.getPower(YanDaoDaoHenPower.POWER_ID).amount / 2;
        }

        // 3. 修改显示数值
        if (bonus > 0) {
            this.magicNumber += bonus;
            this.isMagicNumberModified = true;
        }
    }

    // =========================================================================
    //  【生效逻辑】 传递基础值，让 Power 内部处理加成
    // =========================================================================
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 注意：这里传 baseMagicNumber (20)
        // FenShaoPower 接收到 20 后，会自动加上 bonus (1) = 21
        this.addToBot(new ApplyPowerAction(m, p,
                new FenShaoPower(m, this.baseMagicNumber),
                this.baseMagicNumber));
    }
}