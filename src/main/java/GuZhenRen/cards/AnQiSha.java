package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.AnQiShaAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AnQiSha extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("AnQiSha");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/AnQiSha.png");

    private static final int COST = 1;
    private static final int DAMAGE = 10;

    public AnQiSha() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ENEMY);

        this.setDao(Dao.JIAN_DAO);
        this.baseDamage = DAMAGE;
    }

    private boolean isAttacking(AbstractMonster m) {
        if (m == null) return false;
        return m.intent == AbstractMonster.Intent.ATTACK ||
                m.intent == AbstractMonster.Intent.ATTACK_BUFF ||
                m.intent == AbstractMonster.Intent.ATTACK_DEBUFF ||
                m.intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        boolean isFirstCard = AbstractDungeon.actionManager.cardsPlayedThisTurn.size() <= 1;
        boolean notAttacking = !isAttacking(m);
        boolean trigger = isFirstCard && notAttacking;

        this.addToBot(new AnQiShaAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), trigger));
    }

    @Override
    public void triggerOnGlowCheck() {
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.isEmpty()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upgrade() {
    }
}