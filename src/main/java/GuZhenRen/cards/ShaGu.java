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
    private static final int DAMAGE = 6;
    private static final int HP_LOSS = 1;
    private static final int INITIAL_RANK = 1;

    public ShaGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.ENEMY);

        this.baseDamage = DAMAGE;
        this.magicNumber = this.baseMagicNumber = HP_LOSS;

        this.setDao(Dao.SHA_DAO);

        this.maxRank = 9;
        this.setRank(INITIAL_RANK);

        // 初始化时计算一次，确保载入存档时数值正确
        calculateBaseDamage();
        this.exhaust = false;
    }

    // 重新计算基础伤害：基础值 + 转数加成 + 永久加成
    private void calculateBaseDamage() {
        int rankBonus = Math.max(0, this.rank - 1);
        this.baseDamage = DAMAGE + rankBonus + this.misc;
    }

    @Override
    public void applyPowers() {
        calculateBaseDamage();
        super.applyPowers();
        this.initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        calculateBaseDamage();
        super.calculateCardDamage(mo);
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new LoseHPAction(p, p, this.magicNumber));
        this.addToBot(new ShaGuFatalAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), this.uuid));
    }

    @Override
    public void performUpgradeEffect() {
        calculateBaseDamage();
        this.upgradedDamage = true;
    }

    public static class ShaGuFatalAction extends AbstractGameAction {
        private final DamageInfo info;
        private final UUID targetUUID;
        private static final int INCREASE_AMOUNT = 1;

        public ShaGuFatalAction(AbstractMonster target, DamageInfo info, UUID targetUUID) {
            this.info = info;
            this.setValues(target, info);
            this.actionType = ActionType.DAMAGE;
            this.targetUUID = targetUUID;
        }

        @Override
        public void update() {
            if (this.target != null) {
                AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.SLASH_DIAGONAL));
                this.target.damage(this.info);

                if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead && !this.target.hasPower("Minion")) {

                    // --- 1. 修复大师牌组 (Master Deck) 的逻辑 ---
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (c.uuid.equals(this.targetUUID)) {
                            c.misc += INCREASE_AMOUNT;

                            // 更新基础伤害
                            if (c instanceof ShaGu) {
                                ((ShaGu) c).calculateBaseDamage();
                            }

                            // 【核心修复】
                            // 强制将当前显示伤害 (damage) 重置为 基础伤害 (baseDamage)
                            // 并且清除 "伤害被修改" 的标记 (isDamageModified)
                            // 绝对不要在这里调用 c.applyPowers()，因为它会把当前的力量加成算进去！
                            c.damage = c.baseDamage;
                            c.isDamageModified = false;

                            c.initializeDescription();
                        }
                    }

                    // --- 2. 修复战斗内卡牌 (Hand, Discard, Draw Pile) 的逻辑 ---
                    for (AbstractCard c : GetAllInBattleInstances.get(this.targetUUID)) {
                        c.misc += INCREASE_AMOUNT;

                        // 更新基础伤害
                        if (c instanceof ShaGu) {
                            ((ShaGu) c).calculateBaseDamage();
                        }

                        // 战斗中的卡牌，需要调用 applyPowers()
                        // 这样玩家在当前战斗中就能立刻看到伤害变高了（且包含了力量加成）
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