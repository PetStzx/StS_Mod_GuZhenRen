package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.HemokinesisEffect;

import java.util.UUID;

public class XueShenZi extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("XueShenZi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueShenZi.png");

    private static final int COST = 1;
    private static final int INITIAL_DAMAGE = 6;
    private static final int HP_LOSS = 6;
    private static final int DAMAGE_GROWTH = 6;
    private static final int INITIAL_RANK = 5;

    public XueShenZi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.ENEMY);

        this.setDao(Dao.XUE_DAO);

        this.misc = INITIAL_DAMAGE;

        this.baseMagicNumber = this.magicNumber = HP_LOSS;
        this.baseSecondMagicNumber = this.secondMagicNumber = DAMAGE_GROWTH;

        this.exhaust = true;
        this.setRank(INITIAL_RANK);

        // 初始化时调用一次，确保属性正确
        calculateBaseDamage();
    }

    private void calculateBaseDamage() {
        this.baseDamage = this.misc;
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
    protected void onRankLoaded() {
        // 游戏读取存档后，触发此方法更新数值
        calculateBaseDamage();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new LoseHPAction(p, p, this.magicNumber));
        this.addToBot(new XueShenZiAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), this.secondMagicNumber, this.uuid));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.name = cardStrings.EXTENDED_DESCRIPTION[0];
            this.initializeTitle();

            this.exhaust = false;

            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    // =========================================================================
    //  Action
    // =========================================================================
    public static class XueShenZiAction extends AbstractGameAction {
        private final DamageInfo info;
        private final UUID targetUUID;
        private final int increaseAmount;

        public XueShenZiAction(AbstractCreature target, DamageInfo info, int increaseAmount, UUID targetUUID) {
            this.info = info;
            this.setValues(target, info);
            this.actionType = ActionType.DAMAGE;
            this.duration = Settings.ACTION_DUR_FAST;
            this.increaseAmount = increaseAmount;
            this.targetUUID = targetUUID;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST && this.target != null) {

                AbstractDungeon.effectList.add(new HemokinesisEffect(this.info.owner.hb.cX, this.info.owner.hb.cY, this.target.hb.cX, this.target.hb.cY));
                this.target.damage(this.info);

                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c.uuid.equals(this.targetUUID)) {
                        c.misc += this.increaseAmount;
                        c.applyPowers();
                        c.baseDamage = c.misc;
                        c.isDamageModified = false;
                    }
                }

                for (AbstractCard c : GetAllInBattleInstances.get(this.targetUUID)) {
                    c.misc += this.increaseAmount;
                    c.applyPowers();
                    c.baseDamage = c.misc;
                }

                if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.clearPostCombatActions();
                }
            }
            this.tickDuration();
        }
    }
}