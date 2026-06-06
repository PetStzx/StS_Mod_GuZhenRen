package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.HaoYouPower;
import GuZhenRen.powers.SongYouFadingPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SongYouFengSongBie extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("SongYouFengSongBie");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/SongYouFengSongBie.png");

    private static final int COST = 1;
    private static final int MAGIC = 2; // 2 层消逝

    public SongYouFengSongBie() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ENEMY);

        this.setDao(Dao.FENG_DAO);
        this.baseMagicNumber = this.magicNumber = MAGIC;

        this.exhaust = true;
        this.isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m.hasPower(HaoYouPower.POWER_ID)) {
            this.addToBot(new ApplyPowerAction(m, p, new SongYouFadingPower(m, magicNumber), magicNumber));
            this.addToBot(new RemoveSpecificPowerAction(m, p, HaoYouPower.POWER_ID));
        }
    }
}