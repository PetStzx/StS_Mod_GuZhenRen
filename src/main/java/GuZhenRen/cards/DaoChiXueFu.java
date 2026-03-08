package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect; // 【新增】啃咬特效包

public class DaoChiXueFu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("DaoChiXueFu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/DaoChiXueFu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DAMAGE = 1;
    private static final int HEAL_AMT = 1;
    private static final int UPGRADE_PLUS_HEAL = 1;
    private static final int INITIAL_RANK = 3;

    public DaoChiXueFu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.XUE_DAO);
        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = HEAL_AMT;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        // 1. 索敌与伤害结算
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractMonster validTarget = m;
                if (validTarget == null || validTarget.isDeadOrEscaped() || validTarget.halfDead || validTarget.currentHealth <= 0) {
                    validTarget = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                }

                if (validTarget != null) {
                    DaoChiXueFu.this.calculateCardDamage(validTarget);

                    this.addToTop(new DamageAction(validTarget, new DamageInfo(p, DaoChiXueFu.this.damage, damageTypeForTurn), AttackEffect.NONE));
                    this.addToTop(new VFXAction(new BiteEffect(validTarget.hb.cX, validTarget.hb.cY - 40.0F * Settings.scale, Color.SCARLET.cpy()), 0.3F));
                }
                this.isDone = true;
            }
        });

        // 2. 回复生命
        this.addToBot(new HealAction(p, p, this.magicNumber));

        // 3. 寻找并打出另一张刀翅血蝠
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractMonster nextTarget = m;
                if (nextTarget == null || nextTarget.isDeadOrEscaped() || nextTarget.halfDead || nextTarget.currentHealth <= 0) {
                    nextTarget = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                }

                for (AbstractCard c : p.hand.group) {
                    if (c != DaoChiXueFu.this && c.cardID.equals(DaoChiXueFu.ID)) {

                        p.hand.removeCard(c);
                        AbstractDungeon.getCurrRoom().souls.remove(c);

                        p.limbo.addToBottom(c);
                        c.current_y = -200.0F * Settings.scale;
                        c.target_x = Settings.WIDTH / 2.0F + 200.0F * Settings.scale;
                        c.target_y = Settings.HEIGHT / 2.0F;
                        c.targetAngle = 0.0F;
                        c.lighten(false);
                        c.drawScale = 0.12F;
                        c.targetDrawScale = 0.75F;
                        c.applyPowers();

                        c.freeToPlayOnce = true;

                        AbstractDungeon.actionManager.addToTop(new NewQueueCardAction(c, nextTarget, false, true));
                        AbstractDungeon.actionManager.addToTop(new UnlimboAction(c));

                        break;
                    }
                }
                this.isDone = true;
            }
        });

        // 4. 将 1 张新的刀翅血蝠加入弃牌堆
        AbstractCard batToDiscard = new DaoChiXueFu();
        if (this.upgraded) {
            batToDiscard.upgrade();
        }
        this.addToBot(new MakeTempCardInDiscardAction(batToDiscard, 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.upgradeMagicNumber(UPGRADE_PLUS_HEAL);
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}