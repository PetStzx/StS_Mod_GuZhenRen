package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.SlowPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class WuJinXuanGuangQi extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("WuJinXuanGuangQi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WuJinXuanGuangQi.png");

    private static final int COST = 2;
    private static final int WEAK_AMT = 99;
    private static final int SLOW_AMT = 0;

    public WuJinXuanGuangQi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.LU_DAO); // 律道

        this.exhaust = true; // 消耗

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 遍历所有存活的敌人
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                // 1. 给予 99 层虚弱
                this.addToBot(new ApplyPowerAction(mo, p,
                        new WeakPower(mo, WEAK_AMT, false), WEAK_AMT));

                // 2. 给予 1 层缓慢
                this.addToBot(new ApplyPowerAction(mo, p,
                        new SlowPower(mo, SLOW_AMT), SLOW_AMT));
            }
        }
    }
}