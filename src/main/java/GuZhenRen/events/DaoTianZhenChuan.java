package GuZhenRen.events;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.BaShan;
import GuZhenRen.cards.WanWoDaShouYin;
import GuZhenRen.relics.GuiBuJue;
import GuZhenRen.relics.LuoPoGu;
import GuZhenRen.relics.ShenBuZhi;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;

public class DaoTianZhenChuan extends AbstractImageEvent {
    public static final String ID = GuZhenRen.makeID("DaoTianZhenChuan");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final int HP_LOSS = 8;
    private static final int MIN_GOLD = 90;
    private static final int MAX_GOLD = 120;
    private final int goldReward;
    private final boolean hasKeyCard;

    private enum CurrentScreen {
        INTRO, ZHONGZHOU_CHOICE, BEIYUAN_CHOICE, RESULT
    }

    private CurrentScreen screen = CurrentScreen.INTRO;

    public DaoTianZhenChuan() {
        super(NAME, DESCRIPTIONS[0], GuZhenRen.assetPath("img/events/DaoTianZhenChuan_1.png"));

        this.goldReward = AbstractDungeon.miscRng.random(MIN_GOLD, MAX_GOLD);

        boolean foundKeyCard = false;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.cardID.equals(BaShan.ID) || c.cardID.equals(WanWoDaShouYin.ID)) {
                foundKeyCard = true;
                break;
            }
        }
        this.hasKeyCard = foundKeyCard;

        imageEventText.setDialogOption(String.format(OPTIONS[0], HP_LOSS));
        imageEventText.setDialogOption(String.format(OPTIONS[1], HP_LOSS));
        imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                if (buttonPressed == 0) {
                    // 选择：[前往中洲]
                    AbstractDungeon.player.damage(new com.megacrit.cardcrawl.cards.DamageInfo(null, HP_LOSS, com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS));
                    this.imageEventText.loadImage(GuZhenRen.assetPath("img/events/DaoTianZhenChuan_2.png"));
                    this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                    this.imageEventText.clearAllDialogs();

                    imageEventText.setDialogOption(OPTIONS[3], new ShenBuZhi());
                    imageEventText.setDialogOption(String.format(OPTIONS[4], MIN_GOLD, MAX_GOLD));
                    this.screen = CurrentScreen.ZHONGZHOU_CHOICE;

                } else if (buttonPressed == 1) {
                    // 选择：[前往北原]
                    AbstractDungeon.player.damage(new com.megacrit.cardcrawl.cards.DamageInfo(null, HP_LOSS, com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS));
                    this.imageEventText.loadImage(GuZhenRen.assetPath("img/events/DaoTianZhenChuan_2.png"));
                    this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                    this.imageEventText.clearAllDialogs();

                    imageEventText.setDialogOption(OPTIONS[5], new GuiBuJue());

                    if (this.hasKeyCard) {
                        imageEventText.setDialogOption(OPTIONS[6], new LuoPoGu());
                    } else {
                        imageEventText.setDialogOption(OPTIONS[7], true);
                    }
                    this.screen = CurrentScreen.BEIYUAN_CHOICE;

                } else {
                    // 选择：[无视]
                    this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[8]); // 离开
                    this.screen = CurrentScreen.RESULT;
                }
                break;

            case ZHONGZHOU_CHOICE:
                if (buttonPressed == 0) {
                    // [中洲-开门]
                    this.imageEventText.loadImage(GuZhenRen.assetPath("img/events/DaoTianZhenChuan_3.png"));
                    this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                    AbstractRelic relic = new ShenBuZhi();
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                            (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), relic);
                } else {
                    // [中洲-放弃真传]
                    this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                    AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldReward));
                    AbstractDungeon.player.gainGold(this.goldReward);
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[8]);
                this.screen = CurrentScreen.RESULT;
                break;

            case BEIYUAN_CHOICE:
                if (buttonPressed == 0) {
                    // [北原-开门]
                    this.imageEventText.loadImage(GuZhenRen.assetPath("img/events/DaoTianZhenChuan_3.png"));
                    this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                    AbstractRelic relic = new GuiBuJue();
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                            (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), relic);
                } else {
                    // [北原-收取秘境]
                    this.imageEventText.loadImage(GuZhenRen.assetPath("img/events/DaoTianZhenChuan_4.png"));
                    this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                    AbstractRelic relic = new LuoPoGu();
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                            (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), relic);
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[8]);
                this.screen = CurrentScreen.RESULT;
                break;

            case RESULT:
                this.openMap();
                break;
        }
    }
}