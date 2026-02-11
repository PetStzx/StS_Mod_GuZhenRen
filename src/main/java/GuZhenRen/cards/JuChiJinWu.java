package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances; // 关键导入
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.UUID;

public class JuChiJinWu extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("JuChiJinWu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JuChiJinWu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 2;
    private static final int HITS = 6;   // 初始攻击次数 6
    private static final int UPGRADE_PLUS_HITS = 1; // 升级后 +1 次

    private static final int INITIAL_RANK = 3;

    public JuChiJinWu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ENEMY);

        this.baseDamage = DAMAGE;
        // magicNumber 代表“攻击次数”
        this.baseMagicNumber = this.magicNumber = HITS;

        this.setDao(Dao.JIN_DAO);


        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 循环造成伤害 (次数取决于当前的 magicNumber)
        for (int i = 0; i < this.magicNumber; i++) {
            this.addToBot(new DamageAction(m,
                    new DamageInfo(p, this.damage, this.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        }

        // 2. 将“减少次数”的动作加入队列
        // 只有当次数大于0时才减少，避免减成负数
        if (this.baseMagicNumber > 0) {
            this.addToBot(new DecreaseMagicAction(this.uuid, 1));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.timesUpgraded++;
            this.upgraded = true;
            this.name = cardStrings.EXTENDED_DESCRIPTION[0];
            this.initializeTitle();

            // 升级：6次 -> 7次
            this.upgradeMagicNumber(UPGRADE_PLUS_HITS);

            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    // =========================================================================
    //  自定义 Action
    //  逻辑：通过 UUID 查找当前战斗中的“这一张牌”及其循环使用的副本
    // =========================================================================
    public static class DecreaseMagicAction extends AbstractGameAction {
        private final UUID uuid;
        private final int decreaseAmount;

        public DecreaseMagicAction(UUID targetUUID, int amount) {
            this.uuid = targetUUID;
            this.decreaseAmount = amount;
        }

        @Override
        public void update() {
            // GetAllInBattleInstances.get(this.uuid)
            // 这个方法会返回当前战斗中，所有 UUID 匹配的卡牌。
            // 因为每张牌的 UUID 都是独一无二的（除非你用双发/夜魇复制了它），
            // 所以它绝对不会影响卡组里其他的“锯齿金蜈”。
            for (AbstractCard c : GetAllInBattleInstances.get(this.uuid)) {
                c.baseMagicNumber -= this.decreaseAmount;
                if (c.baseMagicNumber < 0) {
                    c.baseMagicNumber = 0;
                }
                c.magicNumber = c.baseMagicNumber;
                c.applyPowers();
            }

            this.isDone = true;
        }
    }
}