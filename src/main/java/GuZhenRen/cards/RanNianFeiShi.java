package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.FenShaoPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class RanNianFeiShi extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("RanNianFeiShi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/RanNianFeiShi.png");

    private static final int COST = 0;
    private static final int DAMAGE = 4;
    private static final int BURN_AMT = 4;

    public RanNianFeiShi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ENEMY);

        this.setDao(Dao.ZHI_DAO);

        this.baseDamage = DAMAGE;

        this.baseFenShao = this.fenShao = BURN_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        this.addToBot(new ApplyPowerAction(m, p, new FenShaoPower(m, this.fenShao), this.fenShao));
    }

    public void triggerFromExhaustPile() {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractMonster target = AbstractDungeon.getRandomMonster();
                if (target != null && !target.isDeadOrEscaped()) {
                    RanNianFeiShi.this.calculateCardDamage(target);
                    AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(target, AbstractDungeon.player, new FenShaoPower(target, RanNianFeiShi.this.fenShao), RanNianFeiShi.this.fenShao));
                    AbstractDungeon.actionManager.addToTop(new DamageAction(target, new DamageInfo(AbstractDungeon.player, RanNianFeiShi.this.damage, RanNianFeiShi.this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
                this.isDone = true;
            }
        });
    }
}