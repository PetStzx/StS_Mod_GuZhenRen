package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.TaiGuRongYaoZhiGuangPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TianPuGuangHe extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("TianPuGuangHe");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/TianPuGuangHe.png");

    private static final int COST = 1;
    private static final int MAGIC = 150;

    public TianPuGuangHe() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.GUANG_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.exhaust = true; // 消耗
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 除以 25 换算成层数 (150% -> 6层)
        int powerStacks = this.magicNumber / 25;

        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                this.addToBot(new ApplyPowerAction(mo, p, new TaiGuRongYaoZhiGuangPower(mo, powerStacks), powerStacks));
            }
        }
    }
}