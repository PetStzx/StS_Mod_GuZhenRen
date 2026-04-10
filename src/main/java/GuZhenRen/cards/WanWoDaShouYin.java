package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class WanWoDaShouYin extends AbstractShaZhaoCard {

    public static final String ID = GuZhenRen.makeID("WanWoDaShouYin");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WanWoDaShouYin.png");

    private static final int COST = 2;
    private static final int BASE_DAMAGE = 24;
    private static final int MAGIC = 8; // 8倍力量

    public WanWoDaShouYin() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ALL_ENEMY);

        this.baseDamage = BASE_DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.isMultiDamage = true;

        this.setDao(Dao.LI_DAO);
    }

    @Override
    public void applyPowers() {
        AbstractPlayer p = AbstractDungeon.player;
        int realBaseDamage = this.baseDamage;

        if (p != null && p.hasPower(StrengthPower.POWER_ID)) {
            int strAmt = p.getPower(StrengthPower.POWER_ID).amount;
            this.baseDamage += strAmt * (this.magicNumber - 1);
        }

        super.applyPowers();

        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        AbstractPlayer p = AbstractDungeon.player;
        int realBaseDamage = this.baseDamage;

        if (p != null && p.hasPower(StrengthPower.POWER_ID)) {
            int strAmt = p.getPower(StrengthPower.POWER_ID).amount;
            this.baseDamage += strAmt * (this.magicNumber - 1);
        }

        super.calculateCardDamage(mo);

        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAllEnemiesAction(
                p,
                this.multiDamage,
                this.damageTypeForTurn,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        ));
    }
}