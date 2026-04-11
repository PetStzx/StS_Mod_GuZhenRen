package GuZhenRen.relics;
import GuZhenRen.GuZhenRen;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import GuZhenRen.util.ClickableRelic;
import GuZhenRen.util.RestartRunHelper;
//春秋蝉,点击后重置当前层，且本层无法再次使用
public class ChunQiuChan extends ClickableRelic implements CustomSavable<Integer> {
    public static final String ID = GuZhenRen.makeID("ChunQiuChan");
    private static final String IMG = GuZhenRen.assetPath("img/relics/ChunQiuChan.png");
    private static final String IMG_OTL = GuZhenRen.assetPath("img/relics/outline/ChunQiuChan.png");
    //调用父类的构造方法，传参为super(遗物ID,遗物全图，遗物白底图，遗物稀有度，获得遗物时的音效)
    public ChunQiuChan() {
        super(ID, ImageMaster.loadImage(IMG), ImageMaster.loadImage(IMG_OTL), RelicTier.STARTER, LandingSound.CLINK);
    }
    //遗物介绍
    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ChunQiuChan();
    }

    @Override
    public void onVictory(){}

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        //在用户使用牌时触发
    }

    @Override
    public void onRightClick() {
        //在用户右键点击遗物时触发
        if(!usedUp)
            RestartRunHelper.queuedRoomRestart = true;
        this.usedUp();
    }
    @Override
    public Integer onSave() {
        if(this.usedUp) return 1;
        return 0;
    }
    @Override
    public void onLoad(Integer saved) {
        if(saved != null && saved == 1) {
            this.usedUp();
        }
    }
    // @Override
    // public void onPlayerDeath() {
    //     if (AbstractDungeon.getCurrRoom().phase == AbstractDungeon.RoomPhase.COMBAT) {
    //         flash();
    //         AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
    //         AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
    //             @Override
    //             public void update() {
    //                 AbstractDungeon.player.decreaseMaxHealth(1);
    //                 AbstractDungeon.player.heal(1);
    //                 AbstractDungeon.player.experienceLevel += 1;
    //                 AbstractDungeon.player.experience = 0;
    //                 AbstractDungeon.player.experienceNextLevel = 10;
    //                 AbstractDungeon.getCurrRoom().replayEvent();
    //                 isDone = true;
    //             }
    //         });
    //     }
    // }
}
