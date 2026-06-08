package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.WuJinXuanGuangQiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.SlowPower;

public class WuJinXuanGuangQi extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("WuJinXuanGuangQi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WuJinXuanGuangQi.png");

    private static final int COST = 2;

    public WuJinXuanGuangQi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.LU_DAO);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped() && !mo.halfDead) {
                this.addToBot(new ApplyPowerAction(mo, p, new SlowPower(mo, 0), 0));
                this.addToBot(new ApplyPowerAction(mo, p, new WuJinXuanGuangQiPower(mo, 1), 1));
            }
        }
    }
}