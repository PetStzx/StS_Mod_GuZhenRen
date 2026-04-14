package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.ShanYaoPower;
import GuZhenRen.util.BattleStateManager;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;

public class SanShiSanTianGuang extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("SanShiSanTianGuang");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/SanShiSanTianGuang.png");

    private static final int COST = 2;
    private static final int DAMAGE = 4;

    public static int totalShanYaoGainedThisCombat = 0;
    private boolean showDynamicText = false;

    static {
        BattleStateManager.onBattleStart(() -> SanShiSanTianGuang.totalShanYaoGainedThisCombat = 0);
        BattleStateManager.onPostBattle(() -> SanShiSanTianGuang.totalShanYaoGainedThisCombat = 0);
    }

    public SanShiSanTianGuang() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.GUANG_DAO);

        this.baseDamage = DAMAGE;
        this.isMultiDamage = true;
        this.baseSecondMagicNumber = this.secondMagicNumber = 0;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new SanShiSanTianGuangAction(this, p, totalShanYaoGainedThisCombat));
    }

    // ==========================================================
    // 内部动作
    // ==========================================================
    public static class SanShiSanTianGuangAction extends AbstractGameAction {
        private final AbstractCard card;
        private final AbstractPlayer p;
        private final int amountToGain;

        public SanShiSanTianGuangAction(AbstractCard card, AbstractPlayer p, int amountToGain) {
            this.card = card;
            this.p = p;
            this.amountToGain = amountToGain;
        }

        @Override
        public void update() {
            if (p.hasPower(ShanYaoPower.POWER_ID)) {
                this.addToTop(new RemoveSpecificPowerAction(p, p, ShanYaoPower.POWER_ID));
            }

            this.addToTop(new DamageAllEnemiesAction(p, this.card.multiDamage, this.card.damageTypeForTurn, AbstractGameAction.AttackEffect.SLASH_HEAVY));
            this.addToTop(new VFXAction(new BorderFlashEffect(Color.YELLOW, true)));

            if (this.amountToGain > 0) {
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        this.addToTop(new ApplyPowerAction(p, p, new ShanYaoPower(p, amountToGain), amountToGain));
                        this.isDone = true;
                    }
                });
            }
            this.isDone = true;
        }
    }

    @Override
    protected String constructRawDescription() {
        String s = super.constructRawDescription();
        if (this.showDynamicText) {
            s += cardStrings.EXTENDED_DESCRIPTION[0];
        }
        return s;
    }


    @Override
    public void applyPowers() {
        int amount = totalShanYaoGainedThisCombat;
        if (this.secondMagicNumber != amount) {
            this.secondMagicNumber = amount;
            this.isSecondMagicNumberModified = true;
        }
        this.showDynamicText = true;

        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int amount = totalShanYaoGainedThisCombat;
        if (this.secondMagicNumber != amount) {
            this.secondMagicNumber = amount;
            this.isSecondMagicNumberModified = true;
        }
        this.showDynamicText = true;

        super.calculateCardDamage(mo);
    }

    @Override
    public void onMoveToDiscard() {
        this.showDynamicText = false;
        this.initializeDescription();
    }

    @Override
    public void triggerOnExhaust() {
        this.showDynamicText = false;
        this.initializeDescription();
    }
}