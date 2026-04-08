package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.WanWuDaTongBianPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WanWuDaTongBian extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("WanWuDaTongBian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WanWuDaTongBian.png");

    private static final int COST = 2;

    public WanWuDaTongBian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardTarget.SELF);

        this.setDao(Dao.BIAN_HUA_DAO);


        this.baseMagicNumber = this.magicNumber = 1;

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new WanWuDaTongBianPower(p,-1)));
    }
}