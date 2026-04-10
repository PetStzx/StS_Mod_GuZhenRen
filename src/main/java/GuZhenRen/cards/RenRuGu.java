package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame; // 修复：补充了核心包导入
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;

import java.util.ArrayList;

public class RenRuGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("RenRuGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/RenRuGu.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 6;

    // 状态开关：控制是否显示括号
    private boolean showDynamicText = false;

    // 用于跨回合记录血量的时间轴
    public static ArrayList<Integer> hpHistory = new ArrayList<>();

    public RenRuGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);

        this.setDao(Dao.ZHOU_DAO);
        this.setRank(INITIAL_RANK);

        // 初始设为 0
        this.baseSecondMagicNumber = this.secondMagicNumber = 0;

        this.exhaust = true; // 消耗
    }

    // 提取计算目标血量的方法
    private int calculateTargetHp() {
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null || AbstractDungeon.actionManager == null) {
            return 0;
        }

        if (hpHistory.isEmpty()) {
            return AbstractDungeon.player.currentHealth;
        }

        int currentTurn = AbstractDungeon.actionManager.turn;
        int targetIndex = Math.max(0, currentTurn - 2);

        if (targetIndex >= hpHistory.size()) {
            targetIndex = hpHistory.size() - 1;
        }

        return hpHistory.get(targetIndex);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new SFXAction("POWER_TIME_WARP"));
        this.addToBot(new VFXAction(new BorderFlashEffect(Color.ORANGE.cpy(), true)));
        this.addToBot(new VFXAction(new TimeWarpTurnEndEffect(), 0.5F));

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int targetHp = calculateTargetHp();
                int currentHp = p.currentHealth;

                if (targetHp != currentHp) {
                    // 修改底层真实血量
                    p.currentHealth = targetHp;

                    // 防止回溯血量溢出（例如本回合最大生命值减少了）
                    if (p.currentHealth > p.maxHealth) {
                        p.currentHealth = p.maxHealth;
                    }

                    // 刷新血条 UI
                    p.healthBarUpdatedEvent();
                }

                this.isDone = true;
            }
        });
    }

    @Override
    protected String constructRawDescription() {
        String s = super.constructRawDescription();
        if (this.showDynamicText && cardStrings.EXTENDED_DESCRIPTION != null) {
            s += cardStrings.EXTENDED_DESCRIPTION[0];
        }

        return s;
    }

    @Override
    public void applyPowers() {
        int targetHp = calculateTargetHp();
        if (this.secondMagicNumber != targetHp) {
            this.secondMagicNumber = targetHp;
            this.isSecondMagicNumberModified = true;
        }

        this.showDynamicText = true;
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int targetHp = calculateTargetHp();
        if (this.secondMagicNumber != targetHp) {
            this.secondMagicNumber = targetHp;
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

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.selfRetain = true; // 升级保留
            this.upgradeRank(1);    // 6转 -> 7转

            this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}