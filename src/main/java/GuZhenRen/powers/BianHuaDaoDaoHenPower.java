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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class BianHuaDaoDaoHenPower extends AbstractDaoHenPower {
    public static final String POWER_ID = GuZhenRen.makeID("BianHuaDaoDaoHenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final Map<AbstractCard.CardTags, BiFunction<AbstractCreature, Integer, AbstractPower>> DAO_MAP = new HashMap<>();

    static {
        DAO_MAP.put(GuZhenRenTags.LI_DAO, LiDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.YAN_DAO, YanDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.ZHI_DAO, ZhiDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.JIAN_DAO, JianDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.MU_DAO, MuDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.GU_DAO, GuDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.GUANG_DAO, GuangDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.SHA_DAO, ShaDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.XUE_DAO, XueDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.JIN_DAO, JinDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.TOU_DAO, TouDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.LU_DAO, LuDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.FENG_DAO, FengDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.SHI_DAO, ShiDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.YUN_DAO, YunDaoDaoHenPower::new);
        DAO_MAP.put(GuZhenRenTags.ZHOU_DAO, ZhouDaoDaoHenPower::new);
    }

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

        for (AbstractCard.CardTags tag : card.tags) {
            BiFunction<AbstractCreature, Integer, AbstractPower> newFunc = DAO_MAP.get(tag);
            if (newFunc != null) {
                newPowerPrototype = newFunc.apply(this.owner, this.amount);
                break;
            }
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