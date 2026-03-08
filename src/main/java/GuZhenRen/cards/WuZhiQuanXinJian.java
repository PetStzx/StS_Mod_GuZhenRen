package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField; // 【核心】导入StSLib字段
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WuZhiQuanXinJian extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("WuZhiQuanXinJian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WuZhiQuanXinJian.png");

    private static final int COST = 1;
    private static final int DAMAGE = 10;
    private static final int EXHAUSTIVE_AMT = 5; // 消耗性次数

    public WuZhiQuanXinJian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ENEMY);

        // 设置流派为 剑道
        this.setDao(Dao.JIAN_DAO);

        this.baseDamage = DAMAGE;

        // 设置消耗性 5
        // 这会让卡牌在打出 5 次后自动消耗
        ExhaustiveField.ExhaustiveFields.exhaustive.set(this, EXHAUSTIVE_AMT);

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));

        // 2. 伤害翻倍逻辑
        // 直接修改 baseDamage，这样在弃牌堆/抽牌堆中也会保留翻倍后的数值
        this.baseDamage *= 2;

    }
}