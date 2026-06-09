package GuZhenRen.events;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public class DuShiChang extends AbstractImageEvent {
    public static final String ID = GuZhenRen.makeID("DuShiChang");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private enum CurrentScreen {
        INTRO, RESULT
    }

    private CurrentScreen screen = CurrentScreen.INTRO;

    // 每个柜台的中奖次数
    private int commonWins = 0;
    private int uncommonWins = 0;
    private int rareWins = 0;
    private static final int MAX_WINS = 3; // 每个柜台最大中奖次数

    public DuShiChang() {
        super(NAME, DESCRIPTIONS[0], GuZhenRen.assetPath("img/events/DuShiChang.png"));
        updateDialogs();
    }

    private void updateDialogs() {
        this.imageEventText.clearAllDialogs();

        if (this.screen == CurrentScreen.INTRO) {
            int chance = 5;
            String suffixStr = "";

            boolean hasGouShiYun = AbstractDungeon.player.hasRelic(GuZhenRen.makeID("GouShiYun"));
            boolean hasHongYun = AbstractDungeon.player.hasRelic(GuZhenRen.makeID("HongYunQiTianGu"));

            if (hasGouShiYun && hasHongYun) {
                chance += 15;
                suffixStr = OPTIONS[8];
            } else if (hasGouShiYun) {
                chance += 5;
                suffixStr = OPTIONS[6];
            } else if (hasHongYun) {
                chance += 10;
                suffixStr = OPTIONS[7];
            }

            // 选项1: [低等柜台] - 5金币，普通遗物
            if (this.commonWins >= MAX_WINS || isModRelicPoolEmpty(AbstractRelic.RelicTier.COMMON)) {
                this.imageEventText.setDialogOption(OPTIONS[5], true);
            } else if (AbstractDungeon.player.gold < 5) {
                this.imageEventText.setDialogOption(String.format(OPTIONS[4], 5), true);
            } else {
                this.imageEventText.setDialogOption(String.format(OPTIONS[0], chance, suffixStr));
            }

            // 选项2: [常规柜台] - 10金币，罕见遗物
            if (this.uncommonWins >= MAX_WINS || isModRelicPoolEmpty(AbstractRelic.RelicTier.UNCOMMON)) {
                this.imageEventText.setDialogOption(OPTIONS[5], true);
            } else if (AbstractDungeon.player.gold < 10) {
                this.imageEventText.setDialogOption(String.format(OPTIONS[4], 10), true);
            } else {
                this.imageEventText.setDialogOption(String.format(OPTIONS[1], chance, suffixStr));
            }

            // 选项3: [高等柜台] - 15金币，稀有遗物
            if (this.rareWins >= MAX_WINS || isModRelicPoolEmpty(AbstractRelic.RelicTier.RARE)) {
                this.imageEventText.setDialogOption(OPTIONS[5], true);
            } else if (AbstractDungeon.player.gold < 15) {
                this.imageEventText.setDialogOption(String.format(OPTIONS[4], 15), true);
            } else {
                this.imageEventText.setDialogOption(String.format(OPTIONS[2], chance, suffixStr));
            }

            // 选项4: [离开]
            this.imageEventText.setDialogOption(OPTIONS[3]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[3]);
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                if (buttonPressed == 0) {
                    gamble(5, AbstractRelic.RelicTier.COMMON, DESCRIPTIONS[1], DESCRIPTIONS[2]);
                } else if (buttonPressed == 1) {
                    gamble(10, AbstractRelic.RelicTier.UNCOMMON, DESCRIPTIONS[3], DESCRIPTIONS[4]);
                } else if (buttonPressed == 2) {
                    gamble(15, AbstractRelic.RelicTier.RARE, DESCRIPTIONS[5], DESCRIPTIONS[6]);
                } else if (buttonPressed == 3) {
                    this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                    this.screen = CurrentScreen.RESULT;
                    updateDialogs();
                }
                break;
            case RESULT:
                this.openMap();
                break;
        }
    }

    private void gamble(int cost, AbstractRelic.RelicTier tier, String successText, String failText) {
        AbstractDungeon.player.loseGold(cost);
        CardCrawlGame.sound.play("GOLD_JINGLE");

        int chance = 5;
        if (AbstractDungeon.player.hasRelic(GuZhenRen.makeID("GouShiYun"))) chance += 5;
        if (AbstractDungeon.player.hasRelic(GuZhenRen.makeID("HongYunQiTianGu"))) chance += 10;

        if (AbstractDungeon.miscRng.random(1, 100) <= chance) {
            this.imageEventText.updateBodyText(successText);

            AbstractRelic relic = getRandomModRelic(tier);
            if (relic != null) {
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);

                switch (tier) {
                    case COMMON:
                        this.commonWins++;
                        break;
                    case UNCOMMON:
                        this.uncommonWins++;
                        break;
                    case RARE:
                        this.rareWins++;
                        break;
                }
            }
        } else {
            this.imageEventText.updateBodyText(failText);
        }

        updateDialogs();
    }

    private ArrayList<String> getGlobalPool(AbstractRelic.RelicTier tier) {
        switch (tier) {
            case COMMON: return AbstractDungeon.commonRelicPool;
            case UNCOMMON: return AbstractDungeon.uncommonRelicPool;
            case RARE: return AbstractDungeon.rareRelicPool;
            default: return null;
        }
    }

    private boolean isModRelicPoolEmpty(AbstractRelic.RelicTier tier) {
        ArrayList<String> pool = getGlobalPool(tier);
        if (pool == null) return true;

        String modPrefix = GuZhenRen.makeID("");
        for (String id : pool) {
            if (id.startsWith(modPrefix)) {
                return false;
            }
        }
        return true;
    }

    private AbstractRelic getRandomModRelic(AbstractRelic.RelicTier tier) {
        ArrayList<String> pool = getGlobalPool(tier);
        if (pool == null) return null;

        ArrayList<String> modRelics = new ArrayList<>();
        String modPrefix = GuZhenRen.makeID("");

        for (String id : pool) {
            if (id.startsWith(modPrefix)) {
                modRelics.add(id);
            }
        }

        if (modRelics.isEmpty()) return null;

        String chosenId = modRelics.get(AbstractDungeon.miscRng.random(modRelics.size() - 1));
        pool.remove(chosenId);

        return com.megacrit.cardcrawl.helpers.RelicLibrary.getRelic(chosenId).makeCopy();
    }
}