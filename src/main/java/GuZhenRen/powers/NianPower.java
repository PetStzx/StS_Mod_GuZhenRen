package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.RanNianFeiShi;
import GuZhenRen.relics.SiXuRuDianGu;
import GuZhenRen.util.BattleStateManager;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class NianPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = GuZhenRen.makeID("NianPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static int nianGainedThisTurn = 0;
    private static int lastTurn = -1;

    static {
        BattleStateManager.onBattleStart(() -> {
            nianGainedThisTurn = 0;
            lastTurn = -1;
        });
        BattleStateManager.onPostBattle(() -> {
            nianGainedThisTurn = 0;
            lastTurn = -1;
        });
    }

    public static void recordNianGain(int amount) {
        if (amount <= 0) return;
        int currentTurn = 0;
        if (AbstractDungeon.isPlayerInDungeon() && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            currentTurn = AbstractDungeon.actionManager.turn;
        }
        if (currentTurn != lastTurn) {
            nianGainedThisTurn = 0;
            lastTurn = currentTurn;
        }
        nianGainedThisTurn += amount;
    }

    public static int getNianGainedThisTurn() {
        int currentTurn = 0;
        if (AbstractDungeon.isPlayerInDungeon() && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            currentTurn = AbstractDungeon.actionManager.turn;
        }
        if (currentTurn != lastTurn) {
            return 0;
        }
        return nianGainedThisTurn;
    }

    public NianPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/NianPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/NianPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.amount = amount;
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;

        recordNianGain(stackAmount);

        if (stackAmount > 0) {
            // 触发星罗棋布Power
            if (this.owner.hasPower(XingLuoQiBuPower.POWER_ID)) {
                ((XingLuoQiBuPower) this.owner.getPower(XingLuoQiBuPower.POWER_ID)).onGainNian(stackAmount);
            }

            // 触发思绪如电蛊遗物
            if (this.owner.isPlayer && AbstractDungeon.player.hasRelic(SiXuRuDianGu.ID)) {
                ((SiXuRuDianGu) AbstractDungeon.player.getRelic(SiXuRuDianGu.ID)).onGainNian(stackAmount);
            }

            //触发燃念飞石
            triggerRanNianFeiShi();
        }

        checkThreshold();
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        recordNianGain(this.amount);

        if (this.amount > 0) {
            // 触发星罗棋布Power
            if (this.owner.hasPower(XingLuoQiBuPower.POWER_ID)) {
                ((XingLuoQiBuPower) this.owner.getPower(XingLuoQiBuPower.POWER_ID)).onGainNian(this.amount);
            }

            // 触发思绪如电蛊遗物
            if (this.owner.isPlayer && AbstractDungeon.player.hasRelic(SiXuRuDianGu.ID)) {
                ((SiXuRuDianGu) AbstractDungeon.player.getRelic(SiXuRuDianGu.ID)).onGainNian(this.amount);
            }

            //触发燃念飞石
            triggerRanNianFeiShi();
        }

        checkThreshold();
        updateDescription();
    }

    private void triggerRanNianFeiShi() {
        if (AbstractDungeon.player != null && AbstractDungeon.player.exhaustPile != null) {
            for (AbstractCard c : AbstractDungeon.player.exhaustPile.group) {
                if (c instanceof RanNianFeiShi) {
                    ((RanNianFeiShi) c).triggerFromExhaustPile();
                }
            }
        }
    }

    private void checkThreshold() {
        while (this.amount >= 3) {
            this.amount -= 3;
            triggerEffect();
        }

        if (this.amount <= 0) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    private void triggerEffect() {
        this.flash();
        this.addToBot(new DrawCardAction(1));
        this.addToBot(new ApplyPowerAction(owner, owner, new YiPower(owner, 1), 1));
        this.addToBot(new ZhuanYiPower.TriggerAction());
    }

    @Override
    public AbstractPower makeCopy() {
        return new NianPower(owner, amount);
    }

    public static boolean isConverted(AbstractCreature owner) {
        if (owner == null) return false;
        if (owner.hasPower(GuZhenRen.makeID("WanWuDaTongBianPower"))) return true;
        if (owner.hasPower(GuZhenRen.makeID("ZhiZhangPower"))) return true;
        if (owner.hasPower(GuZhenRen.makeID("NianTouShouZuPower"))) return true;
        return false;
    }
}