package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.lang.reflect.Field;

public class BianHuaDaoDaoHenPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("BianHuaDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BianHuaDaoDaoHenPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/BianHuaDaoDaoHenPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/BianHuaDaoDaoHenPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    // =========================================================================
    //  【核心修复】 必须实现 stackPower，否则重复获得时层数不会增加！
    // =========================================================================
    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F; // 让数字跳动一下
        this.amount += stackAmount;
        this.updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        // 1. 预判要转化的目标能力类型
        AbstractPower newPowerPrototype = null;

        if (card.hasTag(GuZhenRenTags.LI_DAO)) {
            newPowerPrototype = new LiDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.YAN_DAO)) {
            newPowerPrototype = new YanDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.ZHI_DAO)) {
            newPowerPrototype = new ZhiDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.JIAN_DAO)) {
            // 【新增】对剑道流派的判断，转化为剑道道痕
            newPowerPrototype = new JianDaoDaoHenPower(this.owner, this.amount);
        }

        if (newPowerPrototype == null) {
            return;
        }

        // 标记该能力是由变化道转化而来
        try {
            Field f = newPowerPrototype.getClass().getField("isFromBianHua");
            f.set(newPowerPrototype, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final AbstractPower finalPowerToApply = newPowerPrototype;

        // 延时执行转化，确保在卡牌效果生效后执行
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractPower currentBianHua = owner.getPower(BianHuaDaoDaoHenPower.POWER_ID);
                        if (currentBianHua != null) {
                            int currentAmount = currentBianHua.amount;

                            // 更新要给予的新能力的层数
                            finalPowerToApply.amount = currentAmount;

                            // 移除旧的变化道
                            addToTop(new RemoveSpecificPowerAction(owner, owner, currentBianHua));
                            // 添加新的道痕
                            addToTop(new ApplyPowerAction(owner, owner, finalPowerToApply, currentAmount));
                        }
                        this.isDone = true;
                    }
                });
                this.isDone = true;
            }
        });
    }
}