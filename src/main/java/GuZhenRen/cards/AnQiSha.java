package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.AnQiShaAction;
import com.badlogic.gdx.graphics.Color; // 【新增导包】用于调用紫色
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
        // 统计本回合打出的攻击牌总数
        int attackCount = 0;
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
            if (c.type == CardType.ATTACK) {
                attackCount++;
            }
        }

        boolean notAttacking = !isAttacking(m);

        boolean trigger = (attackCount == 1) && notAttacking;

        this.addToBot(new AnQiShaAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), trigger));
    }

    @Override
    public void triggerOnGlowCheck() {
        boolean hasPlayedAttack = false;
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
            if (c.type == CardType.ATTACK) {
                hasPlayedAttack = true;
                break;
            }
        }

        boolean hasNonAttackingEnemy = false;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped() && !isAttacking(mo)) {
                hasNonAttackingEnemy = true;
                break;
            }
        }

        if (!hasPlayedAttack && hasNonAttackingEnemy) {
            this.glowColor = Color.PURPLE.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upgrade() {
    }
}