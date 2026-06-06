package GuZhenRen.events;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.NiLiuHuShenYin;
import GuZhenRen.cards.WanLan;
import GuZhenRen.relics.JianChiGu;
import GuZhenRen.relics.NiLiuHe;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NiLiuHeEvent extends AbstractImageEvent {
    public static final String ID = GuZhenRen.makeID("NiLiuHeEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;

    private int progress = 0;
    private static final int MAX_PROGRESS = 18;
    private int consecutiveNoEncounter = 0;

    private final List<Integer> availableEncounters = new ArrayList<>();
    private int currentEncounter = -1;

    private int punchProgress = 0;
    private int fightProgress = 0;

    private enum Screen {
        INTRO, MAIN_RIVER, ENCOUNTER, ENCOUNTER_RESOLVED, ENDING_1, ENDING_1_5, ENDING_2, ENDING_3, LEAVE
    }
    private Screen screen = Screen.INTRO;

    public NiLiuHeEvent() {
        super(NAME, DESCRIPTIONS[0], GuZhenRen.assetPath("img/events/NiLiuHe_1.png"));

        // ==========================================
        // 初始化遭遇池
        // ==========================================
        for (int i = 0; i <= 5; i++) {
            availableEncounters.add(i);
        }

        // 巨石(6) 和 致命旋涡(7) 为负面遭遇，全局仅随机出现其中之一
        if (AbstractDungeon.miscRng.randomBoolean()) {
            availableEncounters.add(6);
        } else {
            availableEncounters.add(7);
        }

        Collections.shuffle(availableEncounters, new java.util.Random(AbstractDungeon.miscRng.randomLong()));

        imageEventText.setDialogOption(String.format(OPTIONS[0], getHpLossAmount()));
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    private int getHpLossAmount() {
        if (progress < 6) return 1;
        if (progress < 12) return 2;
        return 3;
    }

    private String getRiverDescription() {
        if (progress < 6) return DESCRIPTIONS[1]; // 下游
        if (progress < 12) return DESCRIPTIONS[2]; // 中游

        // 上游
        switch (progress) {
            case 12: return DESCRIPTIONS[3];
            case 13: return DESCRIPTIONS[4];
            case 14: return DESCRIPTIONS[5];
            case 15: return DESCRIPTIONS[6];
            case 16: return DESCRIPTIONS[7];
            case 17: return DESCRIPTIONS[8];
            default: return DESCRIPTIONS[3];
        }
    }

    private void leaveRiver() {
        imageEventText.updateBodyText(DESCRIPTIONS[9]);
        imageEventText.clearAllDialogs();
        imageEventText.setDialogOption(OPTIONS[20]); // 离开
        screen = Screen.LEAVE;
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screen) {
            case INTRO:
            case MAIN_RIVER:
            case ENCOUNTER_RESOLVED:
                if (buttonPressed == 0) { // [前进]
                    advanceRiver();
                } else { // [放弃]
                    leaveRiver();
                }
                break;

            case ENCOUNTER:
                handleEncounterInteraction(buttonPressed);
                break;

            case ENDING_1:
                imageEventText.updateBodyText(DESCRIPTIONS[34]);
                imageEventText.clearAllDialogs();
                imageEventText.setDialogOption(OPTIONS[16]);
                screen = Screen.ENDING_1_5;
                break;

            case ENDING_1_5:
                // 真正的发坚持蛊环节
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new JianChiGu());
                imageEventText.updateBodyText(DESCRIPTIONS[35]);
                imageEventText.clearAllDialogs();
                imageEventText.setDialogOption(OPTIONS[17]);
                screen = Screen.ENDING_2;
                break;

            case ENDING_2:
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new NiLiuHe());
                imageEventText.updateBodyText(DESCRIPTIONS[36]);
                imageEventText.clearAllDialogs();

                boolean hasWanLan = false;
                AbstractCard wanLanCard = null;
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c.cardID.equals(WanLan.ID)) {
                        hasWanLan = true;
                        wanLanCard = c;
                        break;
                    }
                }

                if (hasWanLan) {
                    imageEventText.setDialogOption(OPTIONS[18], new NiLiuHuShenYin());
                } else {
                    imageEventText.setDialogOption(OPTIONS[19], true);
                }
                imageEventText.setDialogOption(OPTIONS[20]);
                screen = Screen.ENDING_3;
                break;

            case ENDING_3:
                if (buttonPressed == 0) {
                    this.imageEventText.loadImage(GuZhenRen.assetPath("img/events/NiLiuHe_2.png"));
                    AbstractCard cToRemove = null;
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (c.cardID.equals(WanLan.ID)) {
                            cToRemove = c;
                            break;
                        }
                    }
                    if (cToRemove != null) {
                        AbstractDungeon.player.masterDeck.removeCard(cToRemove);
                    }
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new NiLiuHuShenYin(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    imageEventText.updateBodyText(DESCRIPTIONS[37]);
                } else {
                    imageEventText.updateBodyText(DESCRIPTIONS[38]);
                }
                imageEventText.clearAllDialogs();
                imageEventText.setDialogOption(OPTIONS[20]);
                screen = Screen.LEAVE;
                break;

            case LEAVE:
                this.openMap();
                break;
        }
    }

    private void advanceRiver() {
        AbstractDungeon.player.damage(new DamageInfo(null, getHpLossAmount(), DamageInfo.DamageType.HP_LOSS));
        progress++;

        if (progress >= MAX_PROGRESS) {
            triggerEnding();
            return;
        }

        // 遇到遭遇后的缓冲步
        if (screen == Screen.ENCOUNTER_RESOLVED || screen == Screen.ENCOUNTER) {
            consecutiveNoEncounter++;
            screen = Screen.MAIN_RIVER;
            updateMainRiverUI();
            return;
        }

        // 仅在中下游触发遭遇
        if (progress < 12) {
            int roll = AbstractDungeon.miscRng.random(1, 100);
            if ((roll <= 25 || consecutiveNoEncounter >= 2) && !availableEncounters.isEmpty()) {
                consecutiveNoEncounter = 0; // 触发遭遇，保底计数器清零
                currentEncounter = availableEncounters.remove(0);
                setupEncounterUI();
            } else {
                consecutiveNoEncounter++; // 未触发遭遇，累计保底
                screen = Screen.MAIN_RIVER;
                updateMainRiverUI();
            }
        } else {
            // 上游区域：无遭遇
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
            screen = Screen.MAIN_RIVER;
            updateMainRiverUI();
        }
    }

    private void updateMainRiverUI() {
        imageEventText.updateBodyText(getRiverDescription());
        imageEventText.clearAllDialogs();
        imageEventText.setDialogOption(String.format(OPTIONS[0], getHpLossAmount()));
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    private void triggerEnding() {
        screen = Screen.ENDING_1;
        imageEventText.updateBodyText(DESCRIPTIONS[33]);
        imageEventText.clearAllDialogs();
        imageEventText.setDialogOption(OPTIONS[15]);
    }

    private AbstractRelic relicToLose = null;

    // ==========================================
    // 遭遇池初见布局逻辑
    // ==========================================
    private void setupEncounterUI() {
        screen = Screen.ENCOUNTER;
        imageEventText.clearAllDialogs();

        switch (currentEncounter) {
            case 0: // 力竭蛊仙
                int enc0Dmg = (int)(AbstractDungeon.player.maxHealth * 0.05f);
                imageEventText.updateBodyText(DESCRIPTIONS[10]);
                imageEventText.setDialogOption(String.format(OPTIONS[2], enc0Dmg));
                addStandardOptions();
                break;

            case 1: // 敌对蛊仙
                punchProgress = 0;
                imageEventText.updateBodyText(DESCRIPTIONS[13]);
                imageEventText.setDialogOption(OPTIONS[3]);
                imageEventText.setDialogOption(OPTIONS[1]);
                break;

            case 2: // 八转大能
                fightProgress = 0;
                imageEventText.updateBodyText(DESCRIPTIONS[16]);
                imageEventText.setDialogOption(OPTIONS[5]);
                addStandardOptions();
                break;

            case 3: // 斑斓大虎
                int healAmt = (int)(AbstractDungeon.player.maxHealth * 0.20f);
                imageEventText.updateBodyText(DESCRIPTIONS[19]);
                imageEventText.setDialogOption(String.format(OPTIONS[7], healAmt));
                break;

            case 4: // 捞取蛊虫
                imageEventText.updateBodyText(DESCRIPTIONS[22]);
                imageEventText.setDialogOption(OPTIONS[8]);
                addStandardOptions();
                break;

            case 5: // 捞取遗物
                imageEventText.updateBodyText(DESCRIPTIONS[24]);
                imageEventText.setDialogOption(OPTIONS[9]);
                addStandardOptions();
                break;

            case 6: // 巨石
                imageEventText.updateBodyText(DESCRIPTIONS[26]);
                imageEventText.setDialogOption(String.format(OPTIONS[10], (int)(AbstractDungeon.player.maxHealth * 0.25f)));
                imageEventText.setDialogOption(String.format(OPTIONS[11], (int)(AbstractDungeon.player.maxHealth * 0.08f)));
                imageEventText.setDialogOption(OPTIONS[12], new Injury());
                break;

            case 7: // 致命旋涡
                imageEventText.updateBodyText(DESCRIPTIONS[30]);

                ArrayList<AbstractRelic> removableRelics = new ArrayList<>();
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r.tier != AbstractRelic.RelicTier.STARTER && r.tier != AbstractRelic.RelicTier.SPECIAL) {
                        removableRelics.add(r);
                    }
                }

                if (!removableRelics.isEmpty()) {
                    this.relicToLose = removableRelics.get(AbstractDungeon.miscRng.random(removableRelics.size() - 1));
                    imageEventText.setDialogOption(String.format(OPTIONS[13], this.relicToLose.name), this.relicToLose);
                } else {
                    this.relicToLose = null;
                    imageEventText.setDialogOption(OPTIONS[14]);
                }
                break;
        }
    }

    private void addStandardOptions() {
        imageEventText.setDialogOption(String.format(OPTIONS[0], getHpLossAmount()));
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    // ==========================================
    // 遭遇内选项互动逻辑
    // ==========================================
    private void handleEncounterInteraction(int buttonPressed) {

        switch (currentEncounter) {
            case 0:
                if (buttonPressed == 1) { advanceRiver(); return; }
                if (buttonPressed == 2) { leaveRiver(); return; }

                int goldReward = AbstractDungeon.miscRng.random(50, 80);
                AbstractDungeon.effectList.add(new RainingGoldEffect(goldReward));
                AbstractDungeon.player.gainGold(goldReward);

                if (AbstractDungeon.miscRng.random(1, 100) <= 25) {
                    int enc0Dmg = (int)(AbstractDungeon.player.maxHealth * 0.10f);
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    AbstractDungeon.player.damage(new DamageInfo(null, enc0Dmg, DamageInfo.DamageType.HP_LOSS));
                    imageEventText.updateBodyText(DESCRIPTIONS[12]);
                } else {
                    imageEventText.updateBodyText(DESCRIPTIONS[11]);
                }
                resolveEncounter();
                break;

            case 1:
                if (buttonPressed == 1) { leaveRiver(); return; }

                punchProgress++;

                if (punchProgress <= 2) {
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    AbstractDungeon.player.damage(new DamageInfo(null, 2, DamageInfo.DamageType.HP_LOSS));
                } else {
                    CardCrawlGame.sound.play("ATTACK_HEAVY");
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
                }

                if (punchProgress >= 3) {
                    imageEventText.updateBodyText(DESCRIPTIONS[15]);
                    AbstractDungeon.getCurrRoom().rewards.clear();
                    AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.miscRng.random(10, 20));
                    if (AbstractDungeon.potionRng.random(0, 99) < 40) {
                        AbstractDungeon.getCurrRoom().addPotionToRewards(AbstractDungeon.returnRandomPotion());
                    }
                    AbstractDungeon.combatRewardScreen.open();
                    resolveEncounter();
                } else {
                    imageEventText.updateBodyText(DESCRIPTIONS[14]);
                    imageEventText.clearAllDialogs();
                    if (punchProgress == 2) {
                        imageEventText.setDialogOption(OPTIONS[4]);
                    } else {
                        imageEventText.setDialogOption(OPTIONS[3]);
                    }
                    imageEventText.setDialogOption(OPTIONS[1]);
                }
                break;

            case 2:
                if (fightProgress == 0) {
                    if (buttonPressed == 1) { advanceRiver(); return; }
                    if (buttonPressed == 2) { leaveRiver(); return; }
                }

                fightProgress++;

                if (fightProgress <= 2) {
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
                    AbstractDungeon.player.damage(new DamageInfo(null, 4, DamageInfo.DamageType.HP_LOSS));
                } else {
                    CardCrawlGame.sound.play("BLUNT_HEAVY");
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, false);
                }

                if (fightProgress >= 3) {
                    imageEventText.updateBodyText(DESCRIPTIONS[18]);
                    AbstractDungeon.getCurrRoom().rewards.clear();
                    AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.miscRng.random(25, 35));
                    AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractDungeon.returnRandomRelicTier());
                    AbstractDungeon.cardBlizzRandomizer -= AbstractDungeon.cardBlizzRandomizer / 2;
                    if (AbstractDungeon.potionRng.random(0, 99) < 40) {
                        AbstractDungeon.getCurrRoom().addPotionToRewards(AbstractDungeon.returnRandomPotion());
                    }
                    AbstractDungeon.combatRewardScreen.open();
                    resolveEncounter();
                } else {
                    imageEventText.updateBodyText(DESCRIPTIONS[17]);
                    imageEventText.clearAllDialogs();
                    if (fightProgress == 2) {
                        imageEventText.setDialogOption(OPTIONS[6]);
                    } else {
                        imageEventText.setDialogOption(OPTIONS[5]);
                    }
                }
                break;

            case 3:
                int healAmt = (int)(AbstractDungeon.player.maxHealth * 0.20f);
                if (AbstractDungeon.miscRng.random(1, 100) <= 75) {
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    AbstractDungeon.player.heal(healAmt);
                    imageEventText.updateBodyText(DESCRIPTIONS[20]);
                } else {
                    CardCrawlGame.sound.play("BLUNT_HEAVY");
                    imageEventText.updateBodyText(DESCRIPTIONS[21]);
                }
                resolveEncounter();
                break;

            case 4:
                if (buttonPressed == 1) { advanceRiver(); return; }
                if (buttonPressed == 2) { leaveRiver(); return; }

                AbstractDungeon.player.damage(new DamageInfo(null, 3, DamageInfo.DamageType.HP_LOSS));

                boolean getRelic = AbstractDungeon.miscRng.randomBoolean();
                AbstractRelic guRelic = getRelic ? getRandomGuRelic() : null;

                if (guRelic != null) {
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, guRelic);
                } else {
                    AbstractCard randomGu = getRandomGuCard();
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(randomGu, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                }

                imageEventText.updateBodyText(DESCRIPTIONS[23]);
                resolveEncounter();
                break;

            case 5:
                if (buttonPressed == 1) { advanceRiver(); return; }
                if (buttonPressed == 2) { leaveRiver(); return; }

                AbstractDungeon.player.damage(new DamageInfo(null, 3, DamageInfo.DamageType.HP_LOSS));

                AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());

                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, r);
                imageEventText.updateBodyText(DESCRIPTIONS[25]);
                resolveEncounter();
                break;

            case 6:
                if (buttonPressed == 0) {
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                    AbstractDungeon.player.damage(new DamageInfo(null, (int)(AbstractDungeon.player.maxHealth * 0.25f), DamageInfo.DamageType.NORMAL));
                    imageEventText.updateBodyText(DESCRIPTIONS[27]);
                } else if (buttonPressed == 1) {
                    CardCrawlGame.sound.play("BLUNT_HEAVY");
                    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                    AbstractDungeon.player.decreaseMaxHealth((int)(AbstractDungeon.player.maxHealth * 0.08f));
                    imageEventText.updateBodyText(DESCRIPTIONS[28]);
                } else {
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Injury(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                    imageEventText.updateBodyText(DESCRIPTIONS[29]);
                }
                resolveEncounter();
                break;

            case 7:
                AbstractDungeon.player.damage(new DamageInfo(null, 3, DamageInfo.DamageType.HP_LOSS));
                CardCrawlGame.sound.play("BLUNT_FAST");

                if (this.relicToLose != null && AbstractDungeon.miscRng.randomBoolean()) {
                    AbstractDungeon.player.loseRelic(this.relicToLose.relicId);
                    imageEventText.updateBodyText(String.format(DESCRIPTIONS[32], this.relicToLose.name));
                } else {
                    imageEventText.updateBodyText(DESCRIPTIONS[31]);
                }

                resolveEncounter();
                break;
        }
    }

    private void resolveEncounter() {
        screen = Screen.ENCOUNTER_RESOLVED;
        imageEventText.clearAllDialogs();
        addStandardOptions();
    }

    private AbstractRelic getRandomGuRelic() {
        ArrayList<String> modRelics = new ArrayList<>();
        String modPrefix = GuZhenRen.makeID("");

        if (AbstractDungeon.commonRelicPool != null) {
            for (String id : AbstractDungeon.commonRelicPool) {
                if (id.startsWith(modPrefix)) modRelics.add(id);
            }
        }
        if (AbstractDungeon.uncommonRelicPool != null) {
            for (String id : AbstractDungeon.uncommonRelicPool) {
                if (id.startsWith(modPrefix)) modRelics.add(id);
            }
        }
        if (AbstractDungeon.rareRelicPool != null) {
            for (String id : AbstractDungeon.rareRelicPool) {
                if (id.startsWith(modPrefix)) modRelics.add(id);
            }
        }

        if (modRelics.isEmpty()) {
            return null;
        }

        String chosenId = modRelics.get(AbstractDungeon.miscRng.random(modRelics.size() - 1));

        if (AbstractDungeon.commonRelicPool != null) AbstractDungeon.commonRelicPool.remove(chosenId);
        if (AbstractDungeon.uncommonRelicPool != null) AbstractDungeon.uncommonRelicPool.remove(chosenId);
        if (AbstractDungeon.rareRelicPool != null) AbstractDungeon.rareRelicPool.remove(chosenId);

        return com.megacrit.cardcrawl.helpers.RelicLibrary.getRelic(chosenId).makeCopy();
    }

    private AbstractCard getRandomGuCard() {
        ArrayList<AbstractCard> list = new ArrayList<>();

        Set<String> playerXianGuIDs = new HashSet<>();
        if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck != null) {
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c instanceof AbstractGuZhenRenCard && ((AbstractGuZhenRenCard) c).isXianGu()) {
                    playerXianGuIDs.add(c.cardID);
                }
            }
        }

        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c instanceof AbstractGuZhenRenCard
                    && c.rarity != AbstractCard.CardRarity.BASIC
                    && c.rarity != AbstractCard.CardRarity.SPECIAL) {

                AbstractGuZhenRenCard guCard = (AbstractGuZhenRenCard) c;

                if (guCard.isXianGu() && playerXianGuIDs.contains(guCard.cardID)) {
                    continue;
                }

                list.add(c);
            }
        }

        if (!list.isEmpty()) {
            return list.get(AbstractDungeon.miscRng.random(list.size() - 1)).makeCopy();
        }

        return new Injury();
    }
}