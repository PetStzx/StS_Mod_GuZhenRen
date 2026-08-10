package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.effects.ChunQiuChanFadeEffect;import GuZhenRen.effects.ChunQiuChanStartEffect;import GuZhenRen.util.ChunQiuChanOverlayManager;import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ChunQiuChan extends CustomRelic implements ClickableRelic, CustomSavable<JsonElement> {
    public static final String ID = GuZhenRen.makeID("ChunQiuChan");
    private static final String IMG = GuZhenRen.assetPath("img/relics/ChunQiuChan.png");
    private static final String OUTLINE = GuZhenRen.assetPath("img/relics/outline/ChunQiuChan.png");

    public static boolean shouldFadeFromWhite = false;

    public static class CQCSaveData {
        public ArrayList<String> history = new ArrayList<>();
        public int useCount = 0;
    }

    private ArrayList<String> saveHistory = new ArrayList<>();
    public int useCount = 0;

    public static boolean isTimeTraveling = false;
    private boolean recordedThisFloor = false;

    public static int backupUseCount = -1;
    public static ArrayList<String> backupSaveHistory = null;

    public static boolean pendingCurse = false;

    public ChunQiuChan() {
        super(ID, ImageMaster.loadImage(IMG), new Texture(OUTLINE), RelicTier.RARE, LandingSound.MAGICAL);
        this.counter = -1;
        this.grayscale = false;
        updateDescription();
    }

    @Override
    public String getUpdatedDescription() {
        if (this.counter > 0) {
            return String.format(DESCRIPTIONS[1], this.counter);
        }

        float failChance = 0.05f + (0.10f * this.useCount);
        float synergyBonus = 0.0f;
        boolean hasHongYun = false;
        boolean hasGouShiYun = false;

        if (AbstractDungeon.player != null) {
            if (AbstractDungeon.player.hasRelic(HongYunQiTianGu.ID)) {
                synergyBonus += 0.40f;
                hasHongYun = true;
            }
            if (AbstractDungeon.player.hasRelic(GouShiYun.ID)) {
                synergyBonus += 0.25f;
                hasGouShiYun = true;
            }
        }

        float finalFailChance = Math.max(0.0f, failChance - synergyBonus);
        int failInt = (int) (finalFailChance * 100);

        StringBuilder desc = new StringBuilder(String.format(DESCRIPTIONS[0], failInt));

        if (hasHongYun) {
            desc.append(DESCRIPTIONS[2]);
        }
        if (hasGouShiYun) {
            desc.append(DESCRIPTIONS[3]);
        }

        return desc.toString();
    }

    public void updateDescription() {
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }

    @Override
    public void onEquip() {
        super.onEquip();
        if (saveHistory == null) saveHistory = new ArrayList<>();
        isTimeTraveling = false;

        backupUseCount = -1;
        backupSaveHistory = null;

        recordNode(SaveFile.SaveType.ENTER_ROOM);
        updateDescription();
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        super.justEnteredRoom(room);
        this.recordedThisFloor = false;

        backupUseCount = -1;
        backupSaveHistory = null;

        if (this.counter > 0) {
            this.counter--;
            if (this.counter == 0) {
                this.counter = -1;
                this.grayscale = false;
            }
            updateDescription();
        }

        if (room.phase != AbstractRoom.RoomPhase.COMBAT) {
            recordNode(SaveFile.SaveType.ENTER_ROOM);
        }
    }

    @Override
    public void onVictory() {
        recordNode(SaveFile.SaveType.POST_COMBAT);
    }

    private void recordNode(SaveFile.SaveType saveType) {
        if (this.recordedThisFloor) return;
        if (AbstractDungeon.player == null || AbstractDungeon.currMapNode == null) return;

        ArrayList<String> tempHistory = new ArrayList<>(this.saveHistory);
        this.saveHistory.clear();

        try {
            SaveFile save = new SaveFile(saveType);
            Gson gson = new Gson();
            String json = gson.toJson(save);

            this.saveHistory.addAll(tempHistory);
            this.saveHistory.add(json);

            if (this.saveHistory.size() > 8) {
                this.saveHistory.remove(0);
            }

            this.recordedThisFloor = true;
        } catch (Exception e) {
            GuZhenRen.logger.warn("春秋蝉：当前环境记录节点失败。");
            this.saveHistory.clear();
            this.saveHistory.addAll(tempHistory);
        }
    }

    @Override
    public void onRightClick() {
        if (this.counter > 0 || isTimeTraveling) return;

        if (saveHistory == null || saveHistory.isEmpty()) {
            GuZhenRen.logger.info("春秋蝉：当前没有时间节点可供回溯。");
            CardCrawlGame.sound.play("UI_CLICK_2");
            return;
        }
        if (AbstractDungeon.player == null) return;

        if (AbstractDungeon.isScreenUp) {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SETTINGS ||
                    AbstractDungeon.screen == AbstractDungeon.CurrentScreen.INPUT_SETTINGS) {
                CardCrawlGame.sound.play("UI_CLICK_2");
                return;
            }
        }

        float failChance = 0.05f + (0.10f * this.useCount);
        if (AbstractDungeon.player.hasRelic(HongYunQiTianGu.ID)) {
            failChance -= 0.40f;
        }
        if (AbstractDungeon.player.hasRelic(GouShiYun.ID)) {
            failChance -= 0.25f;
        }
        failChance = Math.max(0.0f, failChance);

        this.useCount++;

        this.counter = 10;
        this.grayscale = true;
        updateDescription();

        boolean isDead = AbstractDungeon.miscRng.randomBoolean(failChance);

        if (isDead) {
            CardCrawlGame.sound.play("ORB_LIGHTNING_EVOKE");
            AbstractDungeon.player.damage(new DamageInfo(null, 999999, DamageInfo.DamageType.HP_LOSS));
            return;
        }

        backupUseCount = this.useCount;
        backupSaveHistory = new ArrayList<>(this.saveHistory);

        shouldFadeFromWhite = true;

        CardCrawlGame.sound.play("STANCE_ENTER_DIVINITY");
        isTimeTraveling = true;

        int backFloors = AbstractDungeon.miscRng.random(4, 6);
        int targetIndex = Math.max(0, saveHistory.size() - 1 - backFloors);
        final String targetJson = saveHistory.get(targetIndex);

        AbstractDungeon.closeCurrentScreen();
        if (AbstractDungeon.dungeonMapScreen != null) {
            AbstractDungeon.dungeonMapScreen.closeInstantly();
        }
        AbstractDungeon.isScreenUp = true;

        AbstractDungeon.topLevelEffects.add(new ChunQiuChanStartEffect(() -> {
            ChunQiuChanOverlayManager.startOverlay();
            com.badlogic.gdx.Gdx.app.postRunnable(() -> {
            try {
                AbstractDungeon.closeCurrentScreen();
                if (AbstractDungeon.dungeonMapScreen != null) {
                    AbstractDungeon.dungeonMapScreen.closeInstantly();
                }

                if (AbstractDungeon.actionManager != null) AbstractDungeon.actionManager.clear();

                if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                    if (AbstractDungeon.player != null && AbstractDungeon.player.powers != null) {
                        for (com.megacrit.cardcrawl.powers.AbstractPower p : AbstractDungeon.player.powers) {
                            p.onVictory();
                        }
                    }
                    if (AbstractDungeon.getMonsters() != null) {
                        for (com.megacrit.cardcrawl.monsters.AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                            if (m != null && m.powers != null) {
                                for (com.megacrit.cardcrawl.powers.AbstractPower p : m.powers) {
                                    p.onVictory();
                                }
                            }
                        }
                    }
                }

                for (com.megacrit.cardcrawl.vfx.AbstractGameEffect e : AbstractDungeon.effectList) { e.dispose(); }
                AbstractDungeon.effectList.clear();

                for (com.megacrit.cardcrawl.vfx.AbstractGameEffect e : AbstractDungeon.topLevelEffects) { e.dispose(); }
                AbstractDungeon.topLevelEffects.clear();

                for (com.megacrit.cardcrawl.vfx.AbstractGameEffect e : AbstractDungeon.topLevelEffectsQueue) { e.dispose(); }
                AbstractDungeon.topLevelEffectsQueue.clear();

                for (com.megacrit.cardcrawl.vfx.AbstractGameEffect e : AbstractDungeon.effectsQueue) { e.dispose(); }
                AbstractDungeon.effectsQueue.clear();

                Gson gson = new Gson();
                SaveFile targetSave = gson.fromJson(targetJson, SaveFile.class);

                for (String relicID : GuZhenRen.recipeRelicIDs) {
                    if (AbstractDungeon.player.hasRelic(relicID) && !targetSave.relics.contains(relicID)) {
                        targetSave.relics.add(relicID);
                        targetSave.relic_counters.add(-1);
                    }
                }

                pendingCurse = true;

                int cqcIndex = targetSave.relics.indexOf(ID);
                if (cqcIndex != -1) {
                    while (targetSave.relic_counters.size() <= cqcIndex) {
                        targetSave.relic_counters.add(-1);
                    }
                    targetSave.relic_counters.set(cqcIndex, 10);
                } else {
                    targetSave.relics.add(ID);
                    targetSave.relic_counters.add(10);
                }

                try {
                    String savePath = SaveAndContinue.getPlayerSavePath(AbstractDungeon.player.chosenClass);
                    String targetJsonData = gson.toJson(targetSave);

                    if (!Settings.isBeta) {
                        targetJsonData = com.megacrit.cardcrawl.saveAndContinue.SaveFileObfuscator.encode(targetJsonData, "key");
                    }

                    com.badlogic.gdx.files.FileHandle fileHandle = com.badlogic.gdx.Gdx.files.local(savePath);
                    fileHandle.writeString(targetJsonData, false);
                } catch (Exception ex) {
                    GuZhenRen.logger.error("春秋蝉：同步写入存档失败！" + ex.getMessage());
                }

                CardCrawlGame.music.fadeAll();
                if (AbstractDungeon.getCurrRoom() != null) AbstractDungeon.getCurrRoom().clearEvent();

                AbstractDungeon.reset();
                CardCrawlGame.loadingSave = true;
                CardCrawlGame.mode = CardCrawlGame.GameMode.CHAR_SELECT;

            } catch (Exception e) {
                GuZhenRen.logger.error("春秋蝉回溯失败：" + e.getMessage());
                isTimeTraveling = false;
            }
        });
        }));
    }

    @Override
    public void update() {
        super.update();

        if (shouldFadeFromWhite && !CardCrawlGame.loadingSave && AbstractDungeon.player != null) {
            shouldFadeFromWhite = false;
            AbstractDungeon.topLevelEffects.add(new ChunQiuChanFadeEffect());
        }

        if (pendingCurse && !CardCrawlGame.loadingSave && AbstractDungeon.player != null) {
            pendingCurse = false;

            AbstractCard curse = com.megacrit.cardcrawl.helpers.CardLibrary.getCard(GuZhenRen.makeID("EYun")).makeCopy();
            AbstractRelic omamori = AbstractDungeon.player.getRelic("Omamori");

            if (omamori != null && omamori.counter > 0) {
                omamori.flash();
                omamori.counter--;
                if (omamori.counter == 0) {
                    omamori.usedUp();
                }
            } else {

                AbstractDungeon.topLevelEffectsQueue.add(
                        new com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect(
                                curse, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, false
                        )
                );
            }
        }
    }

    @Override
    public JsonElement onSave() {
        CQCSaveData data = new CQCSaveData();
        data.history = this.saveHistory;
        data.useCount = this.useCount;
        return new Gson().toJsonTree(data);
    }

    @Override
    public void onLoad(JsonElement element) {
        if (element != null) {
            Gson gson = new Gson();

            if (element.isJsonArray()) {
                Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                this.saveHistory = gson.fromJson(element, listType);
                this.useCount = 0;
            }
            else if (element.isJsonObject()) {
                CQCSaveData data = gson.fromJson(element, CQCSaveData.class);
                this.saveHistory = data.history != null ? data.history : new ArrayList<>();
                this.useCount = data.useCount;
            }
        } else {
            this.saveHistory = new ArrayList<>();
            this.useCount = 0;
        }

        if (backupUseCount != -1) {
            this.useCount = backupUseCount;
            backupUseCount = -1;
        }
        if (backupSaveHistory != null) {
            this.saveHistory = new ArrayList<>(backupSaveHistory);
            backupSaveHistory = null;
        }

        isTimeTraveling = false;
        updateDescription();

        if (this.counter > 0) {
            this.grayscale = true;
        } else {
            this.grayscale = false;
        }
    }

    @Override
    public void setCounter(int setCounter) {
        super.setCounter(setCounter);

        if (this.counter > 0) {
            this.grayscale = true;
        } else {
            this.grayscale = false;
        }

        updateDescription();
    }

    @Override
    public Type savedType() {
        return JsonElement.class;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ChunQiuChan();
    }
}