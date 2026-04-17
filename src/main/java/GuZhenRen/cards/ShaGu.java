package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import java.util.UUID;

public class ShaGu extends AbstractBenMingGuCard {
    public static final String ID = GuZhenRen.makeID("ShaGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ShaGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 1;

    private static final int[] BASE_DMG =          {0, 8, 10, 10, 12, 12, 12, 14, 14, 14};
    private static final int[] PLAY_HP_LOSS =      {0, 1,  1,  1,  1,  1,  1,  1,  1,  1};
    private static final int[] FATAL_MAX_HP_LOSS = {0, 1,  1,  1,  1,  1,  1,  1,  0,  0};
    private static final int[] FATAL_DMG_INC =     {0, 2,  2,  3,  3,  4,  5,  5,  5,  6};

    public ShaGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.ENEMY);

        this.setDao(Dao.SHA_DAO);
        this.maxRank = 9;
        this.setRank(INITIAL_RANK);

        this.exhaust = false;

        calculateStats();
    }

    public void calculateStats() {
        int rankIndex = Math.min(Math.max(this.rank, 1), 9);

        this.baseDamage = BASE_DMG[rankIndex] + this.misc;

        // 绑定第一魔法值（打出掉血）
        this.baseMagicNumber = this.magicNumber = PLAY_HP_LOSS[rankIndex];

        // 绑定第二魔法值（斩杀增伤）
        this.baseSecondMagicNumber = this.secondMagicNumber = FATAL_DMG_INC[rankIndex];

        int fatalMaxHp = FATAL_MAX_HP_LOSS[rankIndex];

        // 动态切换文本格式。因为增伤部分由 !GuZhenRen:SecondMagic! 接管，这里只需填入最大生命流失的 %d
        if (fatalMaxHp > 0) {
            this.myBaseDescription = String.format(cardStrings.EXTENDED_DESCRIPTION[0], fatalMaxHp);
        } else {
            // 8转及以上没有最大生命流失，也没有 %d，直接赋值
            this.myBaseDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        }

        this.initializeDescription();
    }

    @Override
    public void applyPowers() {
        calculateStats();
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        calculateStats();
        super.calculateCardDamage(mo);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        calculateStats();
        int rankIndex = Math.min(Math.max(this.rank, 1), 9);

        this.addToBot(new LoseHPAction(p, p, this.magicNumber));
        // Action 现在直接读取 secondMagicNumber 作为增伤参数
        this.addToBot(new ShaGuFatalAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), this.uuid, FATAL_MAX_HP_LOSS[rankIndex], this.secondMagicNumber));
    }

    @Override
    public void performUpgradeEffect() {
        calculateStats();
        this.upgradedDamage = true;
        this.upgradedSecondMagicNumber = true; // 激活第二魔法值的升级特效
    }

    @Override
    protected void onRankLoaded() {
        calculateStats();
    }

    // =========================================================================
    // 斩杀动作类
    // =========================================================================
    public static class ShaGuFatalAction extends AbstractGameAction {
        private final DamageInfo info;
        private final UUID targetUUID;
        private final int fatalMaxHpLoss;
        private final int fatalDmgInc;

        public ShaGuFatalAction(AbstractMonster target, DamageInfo info, UUID targetUUID, int fatalMaxHpLoss, int fatalDmgInc) {
            this.info = info;
            this.setValues(target, info);
            this.actionType = ActionType.DAMAGE;
            this.targetUUID = targetUUID;
            this.fatalMaxHpLoss = fatalMaxHpLoss;
            this.fatalDmgInc = fatalDmgInc;
        }

        @Override
        public void update() {
            if (this.target != null) {
                AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.SLASH_DIAGONAL));
                this.target.damage(this.info);

                if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead && !this.target.hasPower("Minion")) {

                    if (this.fatalMaxHpLoss > 0) {
                        AbstractDungeon.player.decreaseMaxHealth(this.fatalMaxHpLoss);
                    }

                    // 1. 大师牌组的逻辑
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (c.uuid.equals(this.targetUUID)) {
                            c.misc += this.fatalDmgInc;

                            if (c instanceof ShaGu) {
                                ((ShaGu) c).calculateStats();
                            }

                            c.damage = c.baseDamage;
                            c.isDamageModified = false;
                            c.initializeDescription();
                        }
                    }

                    // 2. 战斗内卡牌的逻辑
                    for (AbstractCard c : GetAllInBattleInstances.get(this.targetUUID)) {
                        c.misc += this.fatalDmgInc;

                        if (c instanceof ShaGu) {
                            ((ShaGu) c).calculateStats();
                        }

                        c.applyPowers();
                    }

                    if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                        AbstractDungeon.actionManager.clearPostCombatActions();
                    }
                }
            }
            this.isDone = true;
        }
    }
}