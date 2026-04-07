package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.FenShaoPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AngryBird extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("AngryBird");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/AngryBird.png");

    private static final int COST = 2;
    private static final int DAMAGE = 19;
    private static final int FEN_SHAO_AMT = 19;

    public AngryBird() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ENEMY);

        this.setDao(Dao.YAN_DAO);

        this.baseDamage = DAMAGE;
        this.baseFenShao = this.fenShao = FEN_SHAO_AMT;

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        this.addToBot(new ApplyPowerAction(m, p,
                new FenShaoPower(m, this.fenShao),
                this.fenShao));
    }
}