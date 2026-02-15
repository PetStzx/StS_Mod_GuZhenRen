package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class JianFengPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("JianFengPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 内部追踪器：判断当前正在结算的牌是否为“剑道牌”
    private boolean isJianDaoActive = false;

    public JianFengPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/JianFengPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/JianFengPower.png");
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        this.updateDescription();
    }

    // =========================================================================
    // 【核心修复1】: 在卡牌刚打出时，将“开启标记”作为一个动作排入队列
    // =========================================================================
    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        final boolean isJianDao = card.hasTag(GuZhenRenTags.JIAN_DAO);

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                JianFengPower.this.isJianDaoActive = isJianDao;
                this.isDone = true;
            }
        });
    }

    // =========================================================================
    // 【核心修复2】: 在卡牌动作添加完毕后，将“关闭标记”作为一个动作排入队列
    // =========================================================================
    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                JianFengPower.this.isJianDaoActive = false;
                this.isDone = true;
            }
        });
    }

    // =========================================================================
    //  剑痕延迟结算
    // =========================================================================
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        // 当前是在剑道队列中，且属于普通攻击伤害，且打中的不是自己
        if (this.isJianDaoActive && info.type == DamageInfo.DamageType.NORMAL && target != this.owner) {
            this.flash();

            // addToBot 会把上剑痕的动作排在动作队列的最末尾
            // 完美实现：所有的伤害都结算完毕后，才触发 +1 +1 +1 的连续剑痕动画
            this.addToBot(new ApplyPowerAction(target, this.owner, new JianHenPower(target, this.amount), this.amount));
        }
    }
}