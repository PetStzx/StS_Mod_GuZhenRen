package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.JianHenPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JianLangSanDie extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("JianLangSanDie");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JianLangSanDie.png");

    private static final int COST = 2;
    private static final int DAMAGE = 4;
    private static final int MAGIC = 4; // 每次给予 4 层剑痕

    public JianLangSanDie() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.JIAN_DAO);

        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;

        this.isMultiDamage = true;

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // “三叠”：排入 3 次专属的波浪动作
        // 每次动作执行时，都会重新计算全场伤害
        for (int i = 0; i < 3; i++) {
            this.addToBot(new JianLangWaveAction(this, p, this.magicNumber));
        }
    }

    @Override
    public void upgrade() {} // 杀招不可升级

    // ==========================================================
    // 内部动作：动态计算群攻叠浪
    // ==========================================================
    public static class JianLangWaveAction extends AbstractGameAction {
        private AbstractCard card;
        private AbstractPlayer p;
        private int magic;

        public JianLangWaveAction(AbstractCard card, AbstractPlayer p, int magic) {
            this.card = card;
            this.p = p;
            this.magic = magic;
        }

        @Override
        public void update() {
            // 1. 在这一浪拍下去的瞬间，让卡牌重新扫描全场敌人
            // 此时它会读取怪物身上最新的“剑痕”层数，并计算出每个怪物分别该受多少伤害
            this.card.calculateCardDamage(null);

            // 2. 将动作排入顶部（addToTop 是后进先出 LIFO）
            // 先打出伤害，然后再挂剑痕，所以需要倒序排入：
            // 先排入挂剑痕（后执行），再排入打伤害（先执行）。

            // (A) 排入群体挂剑痕动作
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    this.addToTop(new ApplyPowerAction(mo, p, new JianHenPower(mo, this.magic), this.magic));
                }
            }

            // (B) 排入群攻伤害动作
            this.addToTop(new DamageAllEnemiesAction(p, this.card.multiDamage, this.card.damageTypeForTurn, AbstractGameAction.AttackEffect.SLASH_HEAVY));

            this.isDone = true;
        }
    }
}