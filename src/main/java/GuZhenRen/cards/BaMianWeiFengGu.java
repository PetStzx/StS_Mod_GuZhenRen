package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.WhirlwindEffect;

import java.util.HashSet;
import java.util.Set;

public class BaMianWeiFengGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("BaMianWeiFengGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/BaMianWeiFengGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 7;
    private static final int UPGRADE_PLUS_DAMAGE = 1; // 升级伤害 7 -> 8
    private static final int MAGIC = 7; // 目标手牌数 7
    private static final int UPGRADE_PLUS_MAGIC = 1;  // 升级目标手牌数 7 -> 8
    private static final int INITIAL_RANK = 7;

    public BaMianWeiFengGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.ALL_ENEMY); // 目标改为所有敌人

        this.setDao(Dao.FENG_DAO);
        this.setRank(INITIAL_RANK);

        this.baseDamage = this.damage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;

        // 【极其重要】：声明此牌为群体伤害，否则 multiDamage 数组不会初始化
        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 排入一个主控 Action 来动态计算需要的抽牌数
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 计算目标手牌数与当前手牌数的差值
                int drawAmt = magicNumber - p.hand.size();

                if (drawAmt > 0) {
                    // 注意这里的 addToTop，后加的先执行：
                    // 所以先排入“结算流派并造成群伤”的后置动作，再排入“抽牌”动作
                    // 实际执行顺序就是：先抽牌 -> 然后判定刚刚抽到了什么牌 -> 打伤害
                    AbstractDungeon.actionManager.addToTop(new BaMianWeiFengFollowUpAction(p, multiDamage, damageTypeForTurn));
                    AbstractDungeon.actionManager.addToTop(new DrawCardAction(p, drawAmt));
                }

                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();

            // 升级后改名为“八面威风蛊”
            this.name = cardStrings.EXTENDED_DESCRIPTION[0];
            this.initializeTitle();

            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1);

            this.initializeDescription();
        }
    }

    // =========================================================================
    // 内部动作类：精准判定实际抽到的流派种类，彻底杜绝多打、少打的 Bug
    // =========================================================================
    public static class BaMianWeiFengFollowUpAction extends AbstractGameAction {
        private AbstractPlayer p;
        private int[] multiDamage;
        private DamageInfo.DamageType damageType;

        public BaMianWeiFengFollowUpAction(AbstractPlayer p, int[] multiDamage, DamageInfo.DamageType damageType) {
            this.p = p;
            this.multiDamage = multiDamage;
            this.damageType = damageType;
        }

        @Override
        public void update() {
            // 使用 Set 集合，自动去重（抽到两张血道牌，Set 里也只会记录一个血道）
            Set<String> uniqueDaos = new HashSet<>();

            // DrawCardAction.drawnCards 记录的是上一瞬间实打实被抽到手里的牌
            for (AbstractCard c : DrawCardAction.drawnCards) {
                // 遍历这张牌的所有 Tag
                for (AbstractCard.CardTags tag : c.tags) {
                    // 只要是以 "_DAO" 结尾的 Tag，就视为一个流派并加入集合
                    if (tag.name().endsWith("_DAO")) {
                        uniqueDaos.add(tag.name());
                    }
                }
            }

            // Set 的大小就是不重复的流派数量
            int times = uniqueDaos.size();

            if (times > 0) {
                for (int i = 0; i < times; i++) {
                    // 倒序加入队列，保证最终在游戏里的播放顺序是：特效 -> 音效 -> 伤害 -> 特效 -> 音效 -> 伤害
                    this.addToTop(new DamageAllEnemiesAction(p, multiDamage, damageType, AttackEffect.SLASH_HORIZONTAL));
                    this.addToTop(new SFXAction("ATTACK_WHIRLWIND"));
                    this.addToTop(new VFXAction(new WhirlwindEffect(new Color(0.9F, 0.9F, 0.9F, 1.0F), true), 0.0F));
                }
            }

            this.isDone = true;
        }
    }
}