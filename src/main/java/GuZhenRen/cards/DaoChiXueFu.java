package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import com.megacrit.cardcrawl.cards.CardGroup;

import java.util.HashSet;
import java.util.UUID;

public class DaoChiXueFu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("DaoChiXueFu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/DaoChiXueFu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 8; // 基础伤害 4 -> 8
    private static final int UPGRADE_PLUS_DAMAGE = 3; // 升级增加伤害 8 -> 11
    private static final int INITIAL_RANK = 3;

    // 状态开关：控制是否显示动态括号文本
    private boolean showDynamicText = false;

    public DaoChiXueFu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.XUE_DAO);
        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = 1;

        this.setRank(INITIAL_RANK);
    }

    // =========================================================================
    // 计算总命中次数：全面雷达 + UUID 去重法
    // =========================================================================
    private int calculateHits() {
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) return 1;

        // 使用 HashSet 来存储 UUID，天然免疫“双发”等机制产生的相同 UUID 的临时假牌
        HashSet<UUID> uniqueBats = new HashSet<>();

        CardGroup[] groupsToCheck = {
                AbstractDungeon.player.hand,
                AbstractDungeon.player.drawPile,
                AbstractDungeon.player.discardPile,
                AbstractDungeon.player.exhaustPile,
                AbstractDungeon.player.limbo
        };

        // 1. 收集五大常规牌堆里的刀翅血蝠
        for (CardGroup group : groupsToCheck) {
            for (AbstractCard c : group.group) {
                if (c.cardID.equals(DaoChiXueFu.ID)) {
                    uniqueBats.add(c.uuid);
                }
            }
        }

        // 2. 收集正在半空中打出的刀翅血蝠 (cardInUse)
        if (AbstractDungeon.player.cardInUse != null && AbstractDungeon.player.cardInUse.cardID.equals(DaoChiXueFu.ID)) {
            uniqueBats.add(AbstractDungeon.player.cardInUse.uuid);
        }

        // 3. 【终极修复】收集被“精炼混沌”、“破坏”等机制抽走，正在动作队列(cardQueue)中排队等待打出的异次元血蝠！
        if (AbstractDungeon.actionManager != null && AbstractDungeon.actionManager.cardQueue != null) {
            for (com.megacrit.cardcrawl.cards.CardQueueItem item : AbstractDungeon.actionManager.cardQueue) {
                if (item.card != null && item.card.cardID.equals(DaoChiXueFu.ID)) {
                    uniqueBats.add(item.card.uuid);
                }
            }
        }

        // 保底命中 1 次
        return Math.max(1, uniqueBats.size());
    }

    // 动态文本显示逻辑
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
        int hits = calculateHits();
        if (this.magicNumber != hits) {
            this.magicNumber = hits;
            this.isMagicNumberModified = true;
        }

        this.showDynamicText = true;
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int hits = calculateHits();
        if (this.magicNumber != hits) {
            this.magicNumber = hits;
            this.isMagicNumberModified = true;
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

    // =========================================================================
    // 打出结算
    // =========================================================================
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int hits = calculateHits();

        for (int i = 0; i < hits; i++) {
            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    if (m != null && !m.isDeadOrEscaped() && m.currentHealth > 0) {
                        DaoChiXueFu.this.calculateCardDamage(m);

                        float offsetX = (float) (Math.random() * 60 - 30) * Settings.scale;
                        float offsetY = (float) (Math.random() * 60 - 30) * Settings.scale;

                        this.addToTop(new DamageAction(m, new DamageInfo(p, DaoChiXueFu.this.damage, damageTypeForTurn), AttackEffect.NONE));
                        this.addToTop(new VFXAction(new BiteEffect(m.hb.cX + offsetX, m.hb.cY - 40.0F * Settings.scale + offsetY, Color.SCARLET.cpy()), 0.15F));
                    }
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE); // 8 -> 11
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}