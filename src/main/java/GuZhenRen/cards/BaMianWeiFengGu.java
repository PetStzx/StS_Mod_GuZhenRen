package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageRandomEnemyAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.WhirlwindEffect;

public class BaMianWeiFengGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("BaMianWeiFengGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/BaMianWeiFengGu.png");

    private static final int COST = 2;
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
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.FENG_DAO);
        this.setRank(INITIAL_RANK);

        this.baseDamage = this.damage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 此时卡牌已经完全进入 Limbo 或弃牌堆，p.hand.size() 是最准确的
                int drawAmt = magicNumber - p.hand.size();

                if (drawAmt > 0) {
                    // 3. 最后排入：多段随机伤害
                    for (int i = 0; i < drawAmt; i++) {
                        AbstractDungeon.actionManager.addToTop(new DamageRandomEnemyAction(
                                new DamageInfo(p, damage, damageTypeForTurn),
                                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL
                        ));
                    }

                    // 2. 其次排入：抽牌动作
                    AbstractDungeon.actionManager.addToTop(new DrawCardAction(p, drawAmt));

                    // 1. 最先排入：音效与特效
                    AbstractDungeon.actionManager.addToTop(new VFXAction(new WhirlwindEffect(new Color(0.9F, 0.9F, 0.9F, 1.0F), true), 0.0F));
                    AbstractDungeon.actionManager.addToTop(new SFXAction("ATTACK_WHIRLWIND"));
                }

                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();

            this.name = cardStrings.EXTENDED_DESCRIPTION[0];
            this.initializeTitle();

            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1);

            this.initializeDescription();
        }
    }
}