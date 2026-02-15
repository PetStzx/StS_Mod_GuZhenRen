package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.JianHenPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JianHenSuoMing extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("JianHenSuoMing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JianHenSuoMing.png");

    private static final int COST = 2;
    private static final int DAMAGE = 0; // 基础伤害为 0

    public JianHenSuoMing() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ENEMY);

        // 设置流派为剑道
        this.setDao(Dao.JIAN_DAO);

        this.baseDamage = DAMAGE;

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获取目标身上的剑痕层数
        int hits = 0;
        if (m != null && m.hasPower(JianHenPower.POWER_ID)) {
            hits = m.getPower(JianHenPower.POWER_ID).amount;
        }

        // 2. 根据剑痕层数，排入对应次数的伤害动作
        if (hits > 0) {
            for (int i = 0; i < hits; i++) {
                this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));
            }
        }

        // 3. 施放完毕后，强制结束回合
        this.addToBot(new PressEndTurnButtonAction());
    }
}