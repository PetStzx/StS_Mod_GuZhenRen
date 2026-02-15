package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class JianDaoDaoHenPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("JianDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    // 标记：是否由变化道转化而来
    public boolean isFromBianHua = false;

    // 内部追踪器：判断当前正在结算的牌是否为“剑道牌” (复用剑锋的逻辑)
    private boolean isJianDaoActive = false;

    public JianDaoDaoHenPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/JianDaoDaoHenPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/JianDaoDaoHenPower.png");

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
        super.stackPower(stackAmount);
        this.updateDescription();
    }

    @Override
    public void onInitialApplication() {
        this.updateDescription();
    }


    //  继承“剑锋”的逻辑：追踪剑道牌并延迟给予剑痕
    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        final boolean isJianDao = card.hasTag(GuZhenRenTags.JIAN_DAO);

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                JianDaoDaoHenPower.this.isJianDaoActive = isJianDao;
                this.isDone = true;
            }
        });
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                JianDaoDaoHenPower.this.isJianDaoActive = false;
                this.isDone = true;
            }
        });
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (this.isJianDaoActive && info.type == DamageInfo.DamageType.NORMAL && target != this.owner) {
            this.flash();
            this.addToBot(new ApplyPowerAction(target, this.owner, new JianHenPower(target, this.amount), this.amount));
        }
    }


    //  变化道回归逻辑：回合结束时变回变化道
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && this.isFromBianHua) {
            this.flash();
            // 1. 移除自己
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            // 2. 变回变化道道痕
            this.addToBot(new ApplyPowerAction(this.owner, this.owner,
                    new BianHuaDaoDaoHenPower(this.owner, this.amount), this.amount));
        }
    }
}