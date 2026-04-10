package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.LengXuePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XueJianLeng extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("XueJianLeng");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueJianLeng.png");

    private static final int COST = 1;
    private static final int MAGIC = 4;

    public XueJianLeng() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.XUE_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo != null && !mo.isDeadOrEscaped()) {
                this.addToBot(new ApplyPowerAction(mo, p, new LengXuePower(mo, this.magicNumber), this.magicNumber));
            }
        }
    }
}