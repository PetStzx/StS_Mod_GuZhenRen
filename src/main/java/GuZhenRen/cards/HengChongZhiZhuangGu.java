package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class HengChongZhiZhuangGu extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("HengChongZhiZhuangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HengChongZhiZhuangGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 7;
    private static final int UPGRADE_PLUS_DAMAGE = 2; // 升级+2， 7 -> 9
    private static final int SELF_DAMAGE = 2;
    private static final int INITIAL_RANK = 4;

    public HengChongZhiZhuangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.LI_DAO);

        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = SELF_DAMAGE;

        this.setRank(INITIAL_RANK);
    }

    // =========================================================================
    // 手动计算反伤的动态显示
    // =========================================================================
    @Override
    public void applyPowers() {
        super.applyPowers();
        calculateSelfDamageDisplay();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        calculateSelfDamageDisplay();
    }

    private void calculateSelfDamageDisplay() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            DamageInfo selfInfo = new DamageInfo(p, this.baseMagicNumber, DamageInfo.DamageType.NORMAL);
            selfInfo.applyPowers(p, p);

            this.magicNumber = selfInfo.output;
            this.isMagicNumberModified = (this.magicNumber != this.baseMagicNumber);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < 2; i++) {
            this.addToBot(new HengChongZhiZhuangAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), this.baseMagicNumber));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    // =========================================================================
    // 自定义动作：分段判定完全格挡与反伤
    // =========================================================================
    public static class HengChongZhiZhuangAction extends AbstractGameAction {
        private final DamageInfo info;
        private final int baseSelfDamage;

        public HengChongZhiZhuangAction(AbstractMonster target, DamageInfo info, int baseSelfDamage) {
            this.info = info;
            this.target = target;
            this.baseSelfDamage = baseSelfDamage;
            this.actionType = ActionType.DAMAGE;
            this.duration = 0.1F;
        }

        @Override
        public void update() {
            if (this.duration == 0.1F && this.target != null) {
                // 在伤害结算前，检测敌人的格挡是否 >= 预计造成的伤害（即完全格挡），并且伤害值本身大于 0
                boolean completelyBlocked = (this.info.output > 0) && (this.target.currentBlock >= this.info.output);

                // 2. 播放特效并造成伤害
                AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.BLUNT_HEAVY));
                this.target.damage(this.info);

                // 3. 判定反伤
                if (completelyBlocked) {
                    AbstractPlayer p = AbstractDungeon.player;

                    DamageInfo selfInfo = new DamageInfo(p, this.baseSelfDamage, DamageInfo.DamageType.NORMAL);
                    selfInfo.applyPowers(p, p);

                    AbstractDungeon.actionManager.addToTop(new DamageAction(p, selfInfo, AttackEffect.BLUNT_LIGHT));
                }

                if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.clearPostCombatActions();
                }
            }
            this.tickDuration();
        }
    }
}