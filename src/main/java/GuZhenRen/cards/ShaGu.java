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

        this.misc = 0;
        calculateBaseDamage();
        this.exhaust = false;
    }

    private void calculateBaseDamage() {
        int rankBonus = Math.max(0, this.rank - 1);
        this.baseDamage = DAMAGE + rankBonus + this.misc;
    }

    @Override
    public void applyPowers() {
        calculateBaseDamage();
        super.applyPowers();
        // 确保数值更新到描述中
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
        // 标记伤害为绿色
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
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (c.uuid.equals(this.targetUUID)) {
                            c.misc += INCREASE_AMOUNT;
                            c.applyPowers();
                            c.isDamageModified = false;
                        }
                    }
                    for (AbstractCard c : GetAllInBattleInstances.get(this.targetUUID)) {
                        c.misc += INCREASE_AMOUNT;
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