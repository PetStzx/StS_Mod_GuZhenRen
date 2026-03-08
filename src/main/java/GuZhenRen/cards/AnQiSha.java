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
    private static final int DAMAGE = 7;

    public AnQiSha() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ENEMY);

        this.setDao(Dao.JIAN_DAO);
        this.baseDamage = DAMAGE;
    }

    // 判断敌人是否正在准备攻击
    private boolean isAttacking(AbstractMonster m) {
        if (m == null) return false;
        return m.intent == AbstractMonster.Intent.ATTACK ||
                m.intent == AbstractMonster.Intent.ATTACK_BUFF ||
                m.intent == AbstractMonster.Intent.ATTACK_DEBUFF ||
                m.intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 条件 1：是否是本回合打出的第一张牌
        boolean isFirstCard = AbstractDungeon.actionManager.cardsPlayedThisTurn.size() <= 1;

        // 条件 2：目标的意图不为攻击
        boolean notAttacking = !isAttacking(m);

        // 综合判定
        boolean trigger = isFirstCard && notAttacking;

        // 将逻辑交给暗歧杀动作结算
        this.addToBot(new AnQiShaAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), trigger));
    }

    // 卡牌边框高亮提示
    @Override
    public void triggerOnGlowCheck() {
        // 在手里捏着还没打出去时，列表应该是空的
        boolean isFirstCard = AbstractDungeon.actionManager.cardsPlayedThisTurn.isEmpty();
        if (isFirstCard) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upgrade() {}
}