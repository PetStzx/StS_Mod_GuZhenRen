package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.relics.LiDaoDaoHen;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;

public class ChiLi extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ChiLi");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ChiLi.png");

    private static final int COST = 2;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DMG = 3;
    private static final int INITIAL_RANK = 6;

    public ChiLi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.ENEMY);

        this.baseDamage = DAMAGE;
        this.exhaust = true;
        this.setDao(Dao.SHI_DAO);

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            this.addToBot(new VFXAction(new BiteEffect(m.hb.cX, m.hb.cY - 40.0F * Settings.scale, Color.SCARLET.cpy()), 0.3F));
        }

        this.addToBot(new ChiLiFatalAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DMG);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    public static class ChiLiFatalAction extends AbstractGameAction {
        private final DamageInfo info;

        public ChiLiFatalAction(AbstractMonster target, DamageInfo info) {
            this.info = info;
            this.setValues(target, info);
            this.actionType = ActionType.DAMAGE;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST && this.target != null) {
                this.target.damage(this.info);

                if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead && !this.target.hasPower("Minion")) {

                    AbstractPlayer p = AbstractDungeon.player;
                    AbstractRelic relic = p.getRelic(LiDaoDaoHen.ID);

                    if (relic != null) {
                        relic.flash();
                        relic.counter++;
                        relic.updateDescription(p.chosenClass);
                        p.reorganizeRelics();
                    } else {
                        LiDaoDaoHen newRelic = new LiDaoDaoHen();
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(p.hb.cX, p.hb.cY, newRelic);
                        newRelic.counter = 1;
                    }

                    this.addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, 1), 1));
                    CardCrawlGame.sound.play("BUFF_1");

                    AbstractDungeon.topLevelEffectsQueue.add(new TextAboveCreatureEffect(
                            p.hb.cX - p.animX,
                            p.hb.cY + p.hb.height / 2.0F,
                            ChiLi.cardStrings.EXTENDED_DESCRIPTION[0],
                            Settings.RED_TEXT_COLOR
                    ));
                }

                if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.clearPostCombatActions();
                }
            }
            this.tickDuration();
        }
    }
}