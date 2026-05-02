package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.RuiYiPower;
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

        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int drawAmt = magicNumber - p.hand.size();

                if (drawAmt > 0) {
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
            Set<String> uniqueDaos = new HashSet<>();

            for (AbstractCard c : DrawCardAction.drawnCards) {
                for (AbstractCard.CardTags tag : c.tags) {
                    if (tag.name().endsWith("_DAO")) {
                        if (RuiYiPower.isActive) {
                            uniqueDaos.add("JIAN_DAO");
                        } else {
                            uniqueDaos.add(tag.name());
                        }
                    }
                }
            }
            int times = uniqueDaos.size();

            if (times > 0) {
                for (int i = 0; i < times; i++) {
                    this.addToTop(new DamageAllEnemiesAction(p, multiDamage, damageType, AttackEffect.SLASH_HORIZONTAL));
                    this.addToTop(new SFXAction("ATTACK_WHIRLWIND"));
                    this.addToTop(new VFXAction(new WhirlwindEffect(new Color(0.9F, 0.9F, 0.9F, 1.0F), true), 0.0F));
                }
            }

            this.isDone = true;
        }
    }
}