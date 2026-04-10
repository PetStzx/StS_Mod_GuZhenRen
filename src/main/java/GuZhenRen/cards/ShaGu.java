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

    // 每次升级带来的伤害加成
    private static final int UPGRADE_PLUS_DMG = 1;

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

        calculateBaseDamage();
        this.exhaust = false;
    }

    private void calculateBaseDamage() {
        int rankBonus = Math.max(0, (this.rank - 1) * UPGRADE_PLUS_DMG);
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

        private static final int INCREASE_AMOUNT = 2;
        private static final int FATAL_HP_LOSS = 2;

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

                // 判定是否斩杀
                if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead && !this.target.hasPower("Minion")) {

                    // 触发失去生命效果
                    AbstractDungeon.actionManager.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, FATAL_HP_LOSS));

                    // 1. 大师牌组的逻辑
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (c.uuid.equals(this.targetUUID)) {
                            c.misc += INCREASE_AMOUNT;

                            if (c instanceof ShaGu) {
                                ((ShaGu) c).calculateBaseDamage();
                            }

                            c.damage = c.baseDamage;
                            c.isDamageModified = false;
                            c.initializeDescription();
                        }
                    }

                    // 2. 战斗内卡牌的逻辑
                    for (AbstractCard c : GetAllInBattleInstances.get(this.targetUUID)) {
                        c.misc += INCREASE_AMOUNT;

                        if (c instanceof ShaGu) {
                            ((ShaGu) c).calculateBaseDamage();
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

    @Override
    protected void onRankLoaded() {
        calculateBaseDamage();
    }
}