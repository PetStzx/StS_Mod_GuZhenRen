package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.relics.LiDaoDaoHen;
import com.badlogic.gdx.graphics.Color; // 【新增】用于定义咬合特效的颜色
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction; // 【新增】用于播放特效动作
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
import com.megacrit.cardcrawl.vfx.combat.BiteEffect; // 【新增】狂宴的咬合特效

public class ChiLi extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ChiLi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
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
        // 【完全复刻 Feed 的特效逻辑】
        if (m != null) {
            // 添加咬合特效：位置修正 (-40.0F) 和 颜色 (SCARLET 猩红) 与原版狂宴一致
            // 0.3F 是特效持续时间，让它在造成伤害前播放
            this.addToBot(new VFXAction(new BiteEffect(m.hb.cX, m.hb.cY - 40.0F * Settings.scale, Color.SCARLET.cpy()), 0.3F));
        }

        // 随后执行伤害与斩杀逻辑
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

    // =========================================================================
    //  内部动作类
    // =========================================================================
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
                // 【注意】这里删除了原本的 FlashAtkImgEffect (砍击特效)
                // 因为我们在 use() 方法里已经添加了 BiteEffect (咬合特效)
                // 如果这里保留砍击，画面会很乱。删掉后就和狂宴一样干净了。

                // 1. 造成伤害
                this.target.damage(this.info);

                // 2. 斩杀判定
                if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead && !this.target.hasPower("Minion")) {

                    AbstractPlayer p = AbstractDungeon.player;
                    AbstractRelic relic = p.getRelic(LiDaoDaoHen.ID);

                    // --- 遗物逻辑：永久成长 ---
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

                    // --- 力量逻辑：本场生效 ---
                    this.addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, 1), 1));

                    // --- 视觉反馈 (手动强制播放，确保战斗结束也能看到) ---

                    // 音效：BUFF_1 是获得力量的声音。
                    // (原版狂宴用的是 HEAL_3，因为它是回血。我们是加力量，所以用 BUFF_1 更合适且听感清晰)
                    CardCrawlGame.sound.play("BUFF_1");

                    // 飘字：红色字体显示 +1 力道道痕
                    AbstractDungeon.topLevelEffectsQueue.add(new TextAboveCreatureEffect(
                            p.hb.cX - p.animX,
                            p.hb.cY + p.hb.height / 2.0F,
                            "+1 力道道痕",
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