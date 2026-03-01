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
        // 判断条件 1：这是否是本回合打出的第一张牌？
        // （注意：在 use 执行时，这张牌本身已经被加入了“本回合打出列表”中，所以长度应为 1）
        boolean isFirstCard = AbstractDungeon.actionManager.cardsPlayedThisTurn.size() <= 1;

        // 判断条件 2：目标的意图不是攻击
        boolean notAttacking = !isAttacking(m);

        // 综合判定
        boolean trigger = isFirstCard && notAttacking;

        // 将逻辑交给专属的暗杀动作去结算
        this.addToBot(new AnQiShaAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), trigger));
    }

    // 卡牌边框高亮提示（方便玩家判断这是否是第一张牌）
    @Override
    public void triggerOnGlowCheck() {
        // 在手里捏着还没打出去时，列表应该是空的
        boolean isFirstCard = AbstractDungeon.actionManager.cardsPlayedThisTurn.isEmpty();
        if (isFirstCard) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy(); // 亮金光
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy(); // 普通蓝光
        }
    }

    @Override
    public void upgrade() {} // 杀招不可升级
}