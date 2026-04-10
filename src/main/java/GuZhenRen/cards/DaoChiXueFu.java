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
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.XUE_DAO);
        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = 1;

        this.setRank(INITIAL_RANK);
    }

    // =========================================================================
    // 计算总命中次数
    // =========================================================================
    private int calculateHits() {
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) return 1;

        int batCount = 0;

        CardGroup[] groupsToCheck = {
                AbstractDungeon.player.hand,
                AbstractDungeon.player.drawPile,
                AbstractDungeon.player.discardPile,
                AbstractDungeon.player.exhaustPile,
                AbstractDungeon.player.limbo
        };

        for (CardGroup group : groupsToCheck) {
            for (AbstractCard c : group.group) {
                if (c.cardID.equals(DaoChiXueFu.ID) && !c.purgeOnUse) {
                    batCount++;
                }
            }
        }

        // 保底命中 1 次
        return Math.max(1, batCount);
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
                    AbstractMonster randomTarget = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);

                    if (randomTarget != null) {
                        DaoChiXueFu.this.calculateCardDamage(randomTarget);

                        float offsetX = (float) (Math.random() * 60 - 30) * Settings.scale;
                        float offsetY = (float) (Math.random() * 60 - 30) * Settings.scale;

                        this.addToTop(new DamageAction(randomTarget, new DamageInfo(p, DaoChiXueFu.this.damage, damageTypeForTurn), AttackEffect.NONE));
                        this.addToTop(new VFXAction(new BiteEffect(randomTarget.hb.cX + offsetX, randomTarget.hb.cY - 40.0F * Settings.scale + offsetY, Color.SCARLET.cpy()), 0.15F));
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
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE); // 8 -> 10
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}