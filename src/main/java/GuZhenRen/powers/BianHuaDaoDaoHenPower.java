package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BianHuaDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("BianHuaDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public BianHuaDaoDaoHenPower(AbstractCreature owner, int amount) {
        super(POWER_ID, powerStrings.NAME, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        AbstractPower newPowerPrototype = null;

        if (card.hasTag(GuZhenRenTags.LI_DAO)) {
            newPowerPrototype = new LiDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.YAN_DAO)) {
            newPowerPrototype = new YanDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.ZHI_DAO)) {
            newPowerPrototype = new ZhiDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.JIAN_DAO)) {
            newPowerPrototype = new JianDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.MU_DAO)) {
            newPowerPrototype = new MuDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.GU_DAO)) {
            newPowerPrototype = new GuDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.GUANG_DAO)) {
            newPowerPrototype = new GuangDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.SHA_DAO)) {
            newPowerPrototype = new ShaDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.XUE_DAO)) {
            newPowerPrototype = new XueDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.JIN_DAO)) {
            newPowerPrototype = new JinDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.TOU_DAO)) {
            newPowerPrototype = new TouDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.LU_DAO)) {
            newPowerPrototype = new LuDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.FENG_DAO)) {
            newPowerPrototype = new FengDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.SHI_DAO)) {
            newPowerPrototype = new ShiDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.YUN_DAO)) {
            newPowerPrototype = new YunDaoDaoHenPower(this.owner, this.amount);
        } else if (card.hasTag(GuZhenRenTags.ZHOU_DAO)) {
            newPowerPrototype = new ZhouDaoDaoHenPower(this.owner, this.amount);
        }


        if (newPowerPrototype == null) {
            return;
        }

        final AbstractPower finalPowerToApply = newPowerPrototype;

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractPower currentBianHua = owner.getPower(BianHuaDaoDaoHenPower.POWER_ID);
                        if (currentBianHua != null) {
                            int currentAmount = currentBianHua.amount;
                            finalPowerToApply.amount = currentAmount;

                            addToTop(new RemoveSpecificPowerAction(owner, owner, currentBianHua));
                            addToTop(new ApplyPowerAction(owner, owner, finalPowerToApply, currentAmount));
                            addToTop(new ZhuanYiPower.TriggerAction());
                        }
                        this.isDone = true;
                    }
                });
                this.isDone = true;
            }
        });
    }
}