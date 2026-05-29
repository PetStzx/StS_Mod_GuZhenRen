package GuZhenRen.events;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.*;
import GuZhenRen.relics.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

// =========================================================================================
// [白骨传承]
// 遇怪率初始为0，跨幕累计。
// 第一幕 (HALL_1)：1个大缸。搜刮 +10% 遇怪率。
// 第二幕 (HALL_2)：3根骨柱。最多搜刮3次，每次搜刮分别 +15%, +30%, +45% 遇怪率。
// 第三幕 (HALL_3)：1具遗骸。搜刮 +20% 遇怪率。
// 第四幕 (PAVILION)：无限搜刮肉囊。每次搜刮 +15% 遇怪率。
//
// 第五幕 (ESCAPE)：
//   - 若全程 0 搜刮，触发[分支路线]获得 战骨车轮 与 杀招-白骨战车。
//   - 若有搜刮，常规逃跑面临抉择：塞1张受伤 / 掉15%最大生命的血 / 牌组有[无足鸟]可无伤逃跑。
//
// 遇敌机制：只要某次搜刮检定遇敌，强制遭遇战斗[双奴隶贩子]。战斗胜利后强制离开，无战斗奖励。
// =========================================================================================

public class BaiGuChuanCheng extends AbstractImageEvent {
    public static final String ID = GuZhenRen.makeID("BaiGuChuanCheng");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private int encounterChance = 0;
    private int totalLootCount = 0;

    private boolean h1Looted = false;
    private boolean h1RewardIsLuoXuan = false;

    private boolean h2Loot1 = false;
    private boolean h2Loot2 = false;
    private boolean h2Loot3 = false;
    private boolean h3Looted = false;
    private boolean pavilionLooted = false;

    private final int hpLoss;

    private enum CurrentScreen {
        INTRO, HALL_1, HALL_2, HALL_3, PAVILION, NORMAL_ESCAPE, ESCAPE_RESULT,
        SECRET_FALL, SECRET_WAKE, SECRET_REWARD, SECRET_ESCAPE, CAUGHT, POST_COMBAT
    }

    private CurrentScreen screen = CurrentScreen.INTRO;

    public BaiGuChuanCheng() {
        super(NAME, DESCRIPTIONS[0], GuZhenRen.assetPath("img/events/BaiGuChuanCheng.png"));
        this.hpLoss = (int) (AbstractDungeon.player.maxHealth * 0.15f);
        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    public void onEnterRoom() {
        CardCrawlGame.sound.play("EVENT_LIVING_WALL");
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                CardCrawlGame.sound.play("EVENT_OOZE");
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);

                this.h1RewardIsLuoXuan = AbstractDungeon.miscRng.randomBoolean();
                this.screen = CurrentScreen.HALL_1;
                updateDialogs();
                break;

            case HALL_1:
                if (buttonPressed == 0) {
                    this.h1Looted = true;
                    boolean giveLuoXuan = this.h1RewardIsLuoXuan;
                    this.h1RewardIsLuoXuan = AbstractDungeon.miscRng.randomBoolean();

                    // [第一幕]搜刮：概率 +10%
                    handleLoot(10, () -> {
                        AbstractCard reward = giveLuoXuan ? new LuoXuanGuQiangGu() : new GuQiangGu();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(reward, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    });
                } else {
                    CardCrawlGame.sound.play("BLUNT_HEAVY");
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);

                    this.screen = CurrentScreen.HALL_2;
                    updateDialogs();
                }
                break;

            case HALL_2:
                int index = 0;

                // [第二幕]搜刮：概率递增 +15% / +30% / +45%
                int currentH2LootCount = (this.h2Loot1 ? 1 : 0) + (this.h2Loot2 ? 1 : 0) + (this.h2Loot3 ? 1 : 0);
                int h2ChanceIncrease = 15;
                if (currentH2LootCount == 1) {
                    h2ChanceIncrease = 30;
                } else if (currentH2LootCount == 2) {
                    h2ChanceIncrease = 45;
                }

                if (!this.h2Loot1) {
                    if (buttonPressed == index) {
                        this.h2Loot1 = true;
                        handleLoot(h2ChanceIncrease, () -> AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new LeiGuDunGu(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F)));
                        return;
                    }
                    index++;
                }
                if (!this.h2Loot2) {
                    if (buttonPressed == index) {
                        this.h2Loot2 = true;
                        handleLoot(h2ChanceIncrease, () -> AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new FeiGuDunGu()));
                        return;
                    }
                    index++;
                }
                if (!this.h2Loot3) {
                    if (buttonPressed == index) {
                        this.h2Loot3 = true;
                        handleLoot(h2ChanceIncrease, () -> AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new BiGuYiGu()));
                        return;
                    }
                    index++;
                }
                if (buttonPressed == index) {
                    this.screen = CurrentScreen.HALL_3;
                    updateDialogs();
                }
                break;

            case HALL_3:
                if (buttonPressed == 0 && !this.h3Looted) {
                    this.h3Looted = true;
                    CardCrawlGame.sound.play("BLOCK_BREAK");
                    // [第三幕]搜刮：概率 +20%
                    handleLoot(20, () -> AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new GuCiGu()));
                } else {
                    CardCrawlGame.sound.play("INTIMIDATE");
                    this.screen = CurrentScreen.PAVILION;
                    updateDialogs();
                }
                break;

            case PAVILION:
                if (buttonPressed == 0) {
                    this.pavilionLooted = true;
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    // [第四幕]搜刮：概率每次 +15%
                    handleLoot(15, this::giveRandomBoneLoot);
                } else {
                    if (this.totalLootCount == 0) {
                        CardCrawlGame.sound.play("ATTACK_MAGIC_BEAM_SHORT");
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, false);
                        this.screen = CurrentScreen.SECRET_FALL;
                    } else {
                        this.screen = CurrentScreen.NORMAL_ESCAPE;
                    }
                    updateDialogs();
                }
                break;

            case SECRET_FALL:
                this.screen = CurrentScreen.SECRET_WAKE;
                updateDialogs();
                break;

            case SECRET_WAKE:
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new ZhanGuCheLun(), Settings.WIDTH / 2.0F - 150f * Settings.scale, Settings.HEIGHT / 2.0F));
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F + 150f * Settings.scale, Settings.HEIGHT / 2.0F, new Recipe_BaiGuZhanChe());
                this.screen = CurrentScreen.SECRET_REWARD;
                updateDialogs();
                break;

            case SECRET_REWARD:
                CardCrawlGame.sound.play("ATTACK_WHIRLWIND");
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.LONG, false);
                this.screen = CurrentScreen.SECRET_ESCAPE;
                updateDialogs();
                break;

            case NORMAL_ESCAPE:
                if (buttonPressed == 0) {
                    CardCrawlGame.sound.play("ATTACK_HEAVY");
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);

                    this.imageEventText.updateBodyText(DESCRIPTIONS[17]);
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Injury(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                } else if (buttonPressed == 1) {
                    CardCrawlGame.sound.play("BLUNT_HEAVY");
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, false);

                    this.imageEventText.updateBodyText(DESCRIPTIONS[18]);
                    AbstractDungeon.player.damage(new com.megacrit.cardcrawl.cards.DamageInfo(null, this.hpLoss));
                } else {
                    this.imageEventText.updateBodyText(DESCRIPTIONS[19]);
                }
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[17]);
                this.screen = CurrentScreen.ESCAPE_RESULT;
                break;

            case CAUGHT:
                AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter("Colosseum Slavers");
                AbstractDungeon.getCurrRoom().rewards.clear();
                AbstractDungeon.getCurrRoom().rewardAllowed = false;
                this.enterCombatFromImage();
                break;

            case SECRET_ESCAPE:
            case ESCAPE_RESULT:
            case POST_COMBAT:
                this.openMap();
                break;
        }
    }

    private void updateDialogs() {
        this.imageEventText.clearAllDialogs();

        switch (this.screen) {
            case HALL_1:
                this.imageEventText.updateBodyText(this.h1Looted ? DESCRIPTIONS[2] : DESCRIPTIONS[1]);

                if (this.h1RewardIsLuoXuan) {
                    this.imageEventText.setDialogOption(String.format(OPTIONS[2], this.encounterChance), new LuoXuanGuQiangGu());
                } else {
                    this.imageEventText.setDialogOption(String.format(OPTIONS[1], this.encounterChance), new GuQiangGu());
                }

                this.imageEventText.setDialogOption(OPTIONS[3]);
                break;

            case HALL_2:
                boolean allLooted = this.h2Loot1 && this.h2Loot2 && this.h2Loot3;
                if (allLooted) {
                    this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                } else if (this.h2Loot1 || this.h2Loot2 || this.h2Loot3) {
                    this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                } else {
                    this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                }

                if (!this.h2Loot1) this.imageEventText.setDialogOption(String.format(OPTIONS[4], this.encounterChance), new LeiGuDunGu());
                if (!this.h2Loot2) this.imageEventText.setDialogOption(String.format(OPTIONS[5], this.encounterChance), new FeiGuDunGu());
                if (!this.h2Loot3) this.imageEventText.setDialogOption(String.format(OPTIONS[6], this.encounterChance), new BiGuYiGu());
                this.imageEventText.setDialogOption(OPTIONS[3]);
                break;

            case HALL_3:
                this.imageEventText.updateBodyText(this.h3Looted ? DESCRIPTIONS[7] : DESCRIPTIONS[6]);
                if (!this.h3Looted) {
                    this.imageEventText.setDialogOption(String.format(OPTIONS[7], this.encounterChance), new GuCiGu());
                }
                this.imageEventText.setDialogOption(OPTIONS[3]);
                break;

            case PAVILION:
                this.imageEventText.updateBodyText(this.pavilionLooted ? DESCRIPTIONS[9] : DESCRIPTIONS[8]);
                this.imageEventText.setDialogOption(String.format(OPTIONS[8], this.encounterChance));
                this.imageEventText.setDialogOption(OPTIONS[3]);
                break;

            case SECRET_FALL:
                this.imageEventText.updateBodyText(DESCRIPTIONS[11]);
                this.imageEventText.setDialogOption(OPTIONS[13]);
                break;

            case SECRET_WAKE:
                this.imageEventText.updateBodyText(DESCRIPTIONS[12]);
                this.imageEventText.setDialogOption(OPTIONS[14], new ZhanGuCheLun());
                break;

            case SECRET_REWARD:
                this.imageEventText.updateBodyText(DESCRIPTIONS[13]);
                this.imageEventText.setDialogOption(OPTIONS[15]);
                break;

            case SECRET_ESCAPE:
                this.imageEventText.updateBodyText(DESCRIPTIONS[14]);
                this.imageEventText.setDialogOption(OPTIONS[17]);
                break;

            case NORMAL_ESCAPE:
                this.imageEventText.updateBodyText(DESCRIPTIONS[10]);
                this.imageEventText.setDialogOption(OPTIONS[9], new Injury());
                this.imageEventText.setDialogOption(String.format(OPTIONS[10], this.hpLoss));

                boolean hasBird = false;
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c.cardID.equals(WuZuNiao.ID)) {
                        hasBird = true;
                        break;
                    }
                }
                if (hasBird) {
                    this.imageEventText.setDialogOption(OPTIONS[11]);
                } else {
                    this.imageEventText.setDialogOption(OPTIONS[12], true); // true 代表将选项置为锁定状态
                }
                break;
        }
    }

    private void handleLoot(int chanceIncrease, Runnable giveReward) {
        this.totalLootCount++;
        giveReward.run();

        if (AbstractDungeon.miscRng.random(0, 99) < this.encounterChance) {
            this.screen = CurrentScreen.CAUGHT;
            this.imageEventText.updateBodyText(DESCRIPTIONS[15]);
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(OPTIONS[16]);
        } else {
            this.encounterChance += chanceIncrease;
            updateDialogs();
        }
    }

    private void giveRandomBoneLoot() {
        ArrayList<Object> pool = new ArrayList<>();
        // 卡牌掉落池
        pool.add(new LeiGuDunGu());
        pool.add(new WuZuNiao());

        // 遗物掉落池
        if (!AbstractDungeon.player.hasRelic(RouBaiGu.ID)) pool.add(new RouBaiGu());
        if (!AbstractDungeon.player.hasRelic(BiGuYiGu.ID)) pool.add(new BiGuYiGu());
        if (!AbstractDungeon.player.hasRelic(TieGuGu.ID)) pool.add(new TieGuGu());
        if (!AbstractDungeon.player.hasRelic(GuCiGu.ID)) pool.add(new GuCiGu());
        if (!AbstractDungeon.player.hasRelic(FeiGuDunGu.ID)) pool.add(new FeiGuDunGu());

        Object reward = pool.get(AbstractDungeon.miscRng.random(pool.size() - 1));

        if (reward instanceof AbstractCard) {
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect((AbstractCard) reward, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
        } else if (reward instanceof AbstractRelic) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, (AbstractRelic) reward);
        }
    }

    @Override
    public void reopen() {
        if (this.screen != CurrentScreen.POST_COMBAT) {
            this.screen = CurrentScreen.POST_COMBAT;
            this.enterImageFromCombat();
            this.imageEventText.updateBodyText(DESCRIPTIONS[16]);
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(OPTIONS[17]);
        }
    }
}