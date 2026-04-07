package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.relics.LiDaoDaoHen;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
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
    private static final int DAMAGE = 9; // 修改为 9 点伤害
    private static final int UPGRADE_PLUS_DMG = 3; // 升级加 3，即 12 点
    private static final int INITIAL_RANK = 6;

    // 状态开关：控制是否显示括号
    private boolean showDynamicText = false;

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

        // 使用 misc 变量来跨战斗追踪斩杀次数，初始需要斩杀 2 次
        this.misc = 2;
        // 把 misc 的值映射到 magicNumber 上，方便在文本中用 !M! 调用
        this.baseMagicNumber = this.magicNumber = this.misc;
    }

    // =========================================================================
    // 动态文本控制逻辑
    // =========================================================================
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
        this.baseMagicNumber = this.magicNumber = this.misc;
        this.showDynamicText = true;
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        this.baseMagicNumber = this.magicNumber = this.misc;
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

    // =========================================================================
    // 打出与斩杀判定
    // =========================================================================
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            this.addToBot(new VFXAction(new BiteEffect(m.hb.cX, m.hb.cY - 40.0F * Settings.scale, Color.SCARLET.cpy()), 0.3F));
        }

        this.addToBot(new ChiLiFatalAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), this));
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

    // =========================================================================
    // 内部动作类：处理斩杀计数与属性增长
    // =========================================================================
    public static class ChiLiFatalAction extends AbstractGameAction {
        private final DamageInfo info;
        private final ChiLi card;

        public ChiLiFatalAction(AbstractMonster target, DamageInfo info, ChiLi card) {
            this.info = info;
            this.card = card;
            this.setValues(target, info);
            this.actionType = ActionType.DAMAGE;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST && this.target != null) {
                this.target.damage(this.info);

                if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead && !this.target.hasPower("Minion")) {

                    card.misc--;

                    if (card.misc <= 0) {
                        card.misc = 2; // 重置为需要 2 次

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
                                ChiLi.cardStrings.EXTENDED_DESCRIPTION[1],
                                Settings.RED_TEXT_COLOR
                        ));
                    }

                    card.baseMagicNumber = card.magicNumber = card.misc;
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (c.uuid.equals(card.uuid)) {
                            c.misc = card.misc;
                            c.baseMagicNumber = c.magicNumber = c.misc;
                            break;
                        }
                    }
                }

                if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.clearPostCombatActions();
                }
            }
            this.tickDuration();
        }
    }
}