package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.cards.QingTiXianYuan;
import GuZhenRen.cards.HongZaoXianYuan;
import GuZhenRen.cards.BaiLiXianYuan;
import GuZhenRen.cards.HuangXingXianYuan;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.powers.PlayerTribulationPower;
import GuZhenRen.tribulations.interfaces.ITribulation;
import GuZhenRen.util.TribulationManager;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.lang.reflect.Type;

public abstract class AbstractKongQiao extends CustomRelic implements CustomSavable<int[]>, ClickableRelic {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("KongQiaoUI"));
    public static final String[] TEXT = uiStrings.TEXT;

    private static final UIStrings globalStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("CardGlobalText"));
    public static final String[] GLOBAL_TEXT = globalStrings.TEXT;

    private static final UIStrings tribulationStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("TribulationNames"));
    public static final String[] TRIBULATION_TEXT = tribulationStrings.TEXT;

    public enum KongQiaoState {
        XP_GATHERING,
        READY_TO_TRIBULATE,
        TRIBULATION_PENDING,
        COUNTDOWN_MODE
    }

    public KongQiaoState currentState = KongQiaoState.XP_GATHERING;

    public int battlesToNextTribulation = 0;
    private static final int BATTLES_PER_TRIBULATION = 2;

    public int xp = 0;
    public int rank = 1;
    protected int neededXP = 1;
    protected String nextRelicID = "";

    public boolean effectUsedThisCombat = false;
    public int completedTribulationIndex = -1;

    public int[] drawCounts = new int[10];

    public AbstractKongQiao(String id, String imgName, RelicTier tier, LandingSound sound) {
        super(
                id,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + imgName)),
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/outline/" + imgName)),
                tier,
                sound
        );
    }

    private int getTypeIndex(String typeName) {
        for (int i = 0; i < TRIBULATION_TEXT.length; i++) {
            if (TRIBULATION_TEXT[i].equals(typeName)) return i;
        }
        return 0;
    }

    protected boolean isTribulationDisabled() {
        if (AbstractDungeon.player == null) return false;
        return AbstractDungeon.player.hasRelic(GuZhenRen.makeID("ShenBuZhi"));
    }

    public void updatePulseStatus() {
        boolean inCombat = false;
        if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null) {
            inCombat = (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT)
                    && !AbstractDungeon.getCurrRoom().isBattleOver;
        }

        if (inCombat) {
            if (!this.effectUsedThisCombat && this.rank > 1 && this.rank <= 5) {
                this.beginLongPulse();
            } else {
                this.stopPulse();
            }
        } else {
            if (!isTribulationDisabled() && (this.currentState == KongQiaoState.READY_TO_TRIBULATE || this.currentState == KongQiaoState.TRIBULATION_PENDING)) {
                this.beginLongPulse();
            } else {
                this.stopPulse();
            }
        }
    }

    public String getNextTribulationName() {
        if (this.rank <= 5) return TRIBULATION_TEXT[0];

        if (this.rank == 6) {
            return (this.xp == 0) ? TRIBULATION_TEXT[0] : TRIBULATION_TEXT[1];
        }
        if (this.rank == 7) {
            return (this.xp == 0) ? TRIBULATION_TEXT[1] : TRIBULATION_TEXT[2];
        }
        if (this.rank == 8) {
            if (this.xp == 0) return TRIBULATION_TEXT[2];
            if (this.xp == 1) return TRIBULATION_TEXT[3];
            return TRIBULATION_TEXT[4];
        }
        if (this.rank >= 9) {
            int step = this.xp % 6;
            if (step < 3) return TRIBULATION_TEXT[2];
            if (step < 5) return TRIBULATION_TEXT[3];
            return TRIBULATION_TEXT[5];
        }

        return TRIBULATION_TEXT[0];
    }

    public static AbstractKongQiao getInstance() {
        if (AbstractDungeon.player == null) return null;
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r instanceof AbstractKongQiao) {
                return (AbstractKongQiao) r;
            }
        }
        return null;
    }

    protected void initStats(int rank, int neededXP, String nextRelicID) {
        this.rank = rank;
        this.neededXP = neededXP;
        this.nextRelicID = nextRelicID;
        this.counter = this.rank;
        updateDescription();
    }

    @Override
    public void onEquip() {
        super.onEquip();
        if (this.rank >= 6 && this.currentState == KongQiaoState.XP_GATHERING) {
            this.currentState = KongQiaoState.COUNTDOWN_MODE;
            this.battlesToNextTribulation = BATTLES_PER_TRIBULATION;
            updateDescription();
        }
        updatePulseStatus();

        if (this.rank >= 6) {
            AbstractDungeon.player.increaseMaxHp(this.rank, true);
        }
    }

    @Override
    public void atPreBattle() {
        this.effectUsedThisCombat = false;
        this.completedTribulationIndex = -1;
        updatePulseStatus();
        updateDescription();
    }

    @Override
    public void atBattleStartPreDraw() {
        if (this.rank >= 6) {
            AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            AbstractCard essence = null;
            switch (this.rank) {
                case 6: essence = new QingTiXianYuan(); break;
                case 7: essence = new HongZaoXianYuan(); break;
                case 8: essence = new BaiLiXianYuan(); break;
                default: essence = new HuangXingXianYuan(); break;
            }
            if (essence != null) {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(essence, 1, false));
            }
        }

        if (this.currentState == KongQiaoState.TRIBULATION_PENDING) {
            if (!isTribulationDisabled()) {
                String typeName = getNextTribulationName();
                int tIndex = getTypeIndex(typeName);

                ITribulation tribulation = TribulationManager.drawTribulation(typeName, this.drawCounts[tIndex]);

                if (tribulation != null) {
                    GuZhenRen.logger.info("[" + typeName + "]正在发生。类型为: " + tribulation.getName());

                    this.drawCounts[tIndex]++;

                    TribulationManager.currentCombatTribulationIndex = tIndex;

                    AbstractDungeon.actionManager.addToTop(
                            new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                                    new PlayerTribulationPower(AbstractDungeon.player, tribulation, typeName))
                    );
                    AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                } else {
                    GuZhenRen.logger.error("未能从管理器获取到灾劫，请检查注册是否成功。");
                }
            }
        }
    }

    @Override
    public void atBattleStart() {
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        super.justEnteredRoom(room);
        updatePulseStatus();
    }

    @Override
    public void onRightClick() {
        if (isTribulationDisabled()) return;

        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            return;
        }

        if (this.rank == 5 || (this.rank == 8 && this.xp >= 2)) {
            if (this.currentState == KongQiaoState.TRIBULATION_PENDING) {
                this.currentState = KongQiaoState.READY_TO_TRIBULATE;
                CardCrawlGame.sound.play("UI_CLICK_1");
                updateDescription();
            } else if (this.currentState == KongQiaoState.READY_TO_TRIBULATE) {
                this.currentState = KongQiaoState.TRIBULATION_PENDING;
                CardCrawlGame.sound.playA("BELL", com.badlogic.gdx.math.MathUtils.random(-0.4F, -0.2F));
                updateDescription();
            }
        }

        updatePulseStatus();
    }

    @Override
    public void onVictory() {
        if (this.currentState == KongQiaoState.TRIBULATION_PENDING) {
            if (!isTribulationDisabled()) {
                onTribulationSuccess();
            }
            updatePulseStatus();
            return;
        }

        if (this.currentState == KongQiaoState.XP_GATHERING) {
            AbstractRoom room = AbstractDungeon.getCurrRoom();
            if (room instanceof MonsterRoomBoss) gainXP(5);
            else if (room instanceof MonsterRoomElite) gainXP(3);
            else gainXP(1);
        }
        else if (this.currentState == KongQiaoState.COUNTDOWN_MODE && !isTribulationDisabled()) {
            this.battlesToNextTribulation--;
            if (this.battlesToNextTribulation <= 0) {
                this.currentState = KongQiaoState.TRIBULATION_PENDING;
                CardCrawlGame.sound.playA("BELL", com.badlogic.gdx.math.MathUtils.random(-0.4F, -0.2F));
                this.flash();
            }
            updateDescription();
        }

        updatePulseStatus();
    }

    public void onTribulationSuccess() {
        this.completedTribulationIndex = TribulationManager.currentCombatTribulationIndex;
        if (this.rank < 6) {
            evolve(0);
        } else {
            this.xp += 1;
            if (this.xp >= this.neededXP) {
                evolve(0);
            } else {
                this.currentState = KongQiaoState.COUNTDOWN_MODE;
                this.battlesToNextTribulation = BATTLES_PER_TRIBULATION;

                if (this.rank == 8 && this.xp == 2) {
                    this.currentState = KongQiaoState.TRIBULATION_PENDING;
                    CardCrawlGame.sound.playA("BELL", com.badlogic.gdx.math.MathUtils.random(-0.4F, -0.2F));
                    AbstractDungeon.topLevelEffectsQueue.add(new BorderFlashEffect(Color.WHITE.cpy()));
                }
                updateDescription();
            }
        }
    }

    @Override
    public int changeNumberOfCardsInReward(int numberOfCards) {
        if (this.completedTribulationIndex != -1) {
            return numberOfCards + 1;
        }
        return numberOfCards;
    }

    public void gainXP(int amount) {
        if (this.rank >= 6) return;
        if (nextRelicID == null || nextRelicID.isEmpty()) return;
        this.xp += amount;
        this.flash();

        if (this.xp >= this.neededXP) {
            if (this.rank == 5) {
                this.xp = this.neededXP;
                if (this.currentState == KongQiaoState.XP_GATHERING) {
                    this.currentState = KongQiaoState.TRIBULATION_PENDING;
                    CardCrawlGame.sound.playA("BELL", com.badlogic.gdx.math.MathUtils.random(-0.4F, -0.2F));
                    AbstractDungeon.topLevelEffectsQueue.add(new BorderFlashEffect(Color.WHITE.cpy()));
                }
            } else {
                int overflowXP = this.xp - this.neededXP;
                evolve(overflowXP);
            }
        }
        updateDescription();
        updatePulseStatus();
    }

    private void evolve(int overflowXP) {
        GuZhenRen.logger.info("空窍突破。目标: " + this.nextRelicID);

        int relicIndex = AbstractDungeon.player.relics.indexOf(this);
        AbstractRelic newRelic = RelicLibrary.getRelic(this.nextRelicID).makeCopy();

        if (newRelic instanceof AbstractKongQiao) {
            AbstractKongQiao nextKongQiao = (AbstractKongQiao) newRelic;
            nextKongQiao.xp = overflowXP;

            nextKongQiao.completedTribulationIndex = this.completedTribulationIndex;

            System.arraycopy(this.drawCounts, 0, nextKongQiao.drawCounts, 0, this.drawCounts.length);

            if (nextKongQiao.rank >= 6) {
                nextKongQiao.currentState = KongQiaoState.COUNTDOWN_MODE;
                nextKongQiao.battlesToNextTribulation = BATTLES_PER_TRIBULATION;
            }

            nextKongQiao.updateDescription();
            autoUpgradeVitalGu(nextKongQiao.rank);
        }

        newRelic.instantObtain(AbstractDungeon.player, relicIndex, true);
        newRelic.flash();
    }

    @Override
    public String getUpdatedDescription() {
        if (this.DESCRIPTIONS == null || this.DESCRIPTIONS.length == 0) return "";
        String baseDesc = this.DESCRIPTIONS[0];
        StringBuilder extra = new StringBuilder();

        boolean inCombat = false;
        if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null) {
            inCombat = (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT)
                    && !AbstractDungeon.getCurrRoom().isBattleOver;
        }

        if (this.currentState == KongQiaoState.READY_TO_TRIBULATE) {
            extra.append(TEXT[11]);
        } else if (this.currentState == KongQiaoState.TRIBULATION_PENDING) {
            if (inCombat && !isTribulationDisabled()) {
                extra.append(String.format(TEXT[7], getNextTribulationName()));
            } else {
                extra.append(String.format(TEXT[4], getNextTribulationName()));
                appendBreakthroughHint(extra);

                if (this.rank == 5) {
                    extra.append(TEXT[3]);
                } else if (this.rank == 8 && this.xp == 2) {
                    extra.append(TEXT[6]);
                }
            }
        } else if (this.currentState == KongQiaoState.COUNTDOWN_MODE) {
            extra.append(String.format(TEXT[5], getNextTribulationName(), this.battlesToNextTribulation));
            appendBreakthroughHint(extra);
        } else {
            if (this.rank < 6) {
                int remaining = neededXP - xp;
                if (remaining < 0) remaining = 0;

                if (this.rank == 5) {
                    extra.append(String.format(TEXT[2], remaining));
                } else if (nextRelicID != null && !nextRelicID.isEmpty()) {
                    extra.append(String.format(TEXT[0], remaining));
                }
            }
        }

        return baseDesc + extra.toString();
    }

    private void appendBreakthroughHint(StringBuilder extra) {
        if (this.rank == 6 && this.xp == 1) {
            extra.append(String.format(TEXT[8], TRIBULATION_TEXT[1], GLOBAL_TEXT[6]));
        } else if (this.rank == 7 && this.xp == 1) {
            extra.append(String.format(TEXT[8], TRIBULATION_TEXT[2], GLOBAL_TEXT[7]));
        } else if (this.rank == 8) {
            if (this.xp == 1) {
                extra.append(String.format(TEXT[9], TRIBULATION_TEXT[3]));
            } else if (this.xp == 2) {
                extra.append(String.format(TEXT[8], TRIBULATION_TEXT[4], GLOBAL_TEXT[8]));
            }
        } else if (this.rank == 9) {
            if (this.xp == 17) {
                extra.append(String.format(TEXT[8], TRIBULATION_TEXT[5], TEXT[10]));
            }
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!this.effectUsedThisCombat && this.rank > 1 && this.rank <= 5) {
            if (card instanceof AbstractGuZhenRenCard) {
                int cardRank = ((AbstractGuZhenRenCard) card).rank;
                if (cardRank >= 1 && cardRank < this.rank) {
                    this.effectUsedThisCombat = true;
                    this.flash();

                    updatePulseStatus();

                    for (AbstractCard c : AbstractDungeon.player.hand.group) {
                        if (c != card && c instanceof AbstractGuZhenRenCard) {
                            ((AbstractGuZhenRenCard) c).isKongQiaoFree = false;
                            c.freeToPlayOnce = false;
                            c.applyPowers();
                        }
                    }
                }
            }
        }
    }

    public void updateDescription() {
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));

        if (this.rank < 5 || (this.rank == 5 && this.currentState == KongQiaoState.XP_GATHERING)) {
            addKeywordTip(TEXT[1]);
        }

        if (this.currentState == KongQiaoState.COUNTDOWN_MODE || this.currentState == KongQiaoState.TRIBULATION_PENDING) {
            String tribulationName = getNextTribulationName();
            addKeywordTip(tribulationName);
        }

        if (this.rank > 1 && this.rank <= 5) {
            String rankKeyword = getRankKeywordName(this.rank);
            addKeywordTip(rankKeyword);
        }
    }

    private void addKeywordTip(String keywordName) {
        if (keywordName == null) return;
        String description = null;
        if (GameDictionary.keywords.containsKey(keywordName)) {
            description = GameDictionary.keywords.get(keywordName);
        } else if (GameDictionary.keywords.containsKey("guzhenren:" + keywordName)) {
            description = GameDictionary.keywords.get("guzhenren:" + keywordName);
        } else if (GameDictionary.keywords.containsKey(keywordName.toLowerCase())) {
            description = GameDictionary.keywords.get(keywordName.toLowerCase());
        }
        if (description != null) {
            this.tips.add(new PowerTip(TipHelper.capitalize(keywordName), description));
        }
    }

    private String getRankKeywordName(int r) {
        if (r >= 1 && r <= 9) {
            return GLOBAL_TEXT[r - 1];
        }
        return GLOBAL_TEXT[0];
    }

    private void autoUpgradeVitalGu(int targetRank) {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.hasTag(GuZhenRenTags.BEN_MING_GU) && c instanceof AbstractGuZhenRenCard) {
                AbstractGuZhenRenCard guCard = (AbstractGuZhenRenCard) c;
                boolean upgraded = false;
                while (guCard.rank < targetRank && guCard.canUpgrade()) {
                    guCard.upgrade();
                    upgraded = true;
                }
                if (upgraded) {
                    float x = Settings.WIDTH / 2.0F;
                    float y = Settings.HEIGHT / 2.0F;
                    AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(guCard.makeStatEquivalentCopy(), x, y));
                    CardCrawlGame.sound.play("CARD_UPGRADE");
                }
            }
        }
    }

    public static int getCurrentRank() {
        if (AbstractDungeon.player == null) return 1;
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r instanceof AbstractKongQiao) {
                return ((AbstractKongQiao) r).rank;
            }
        }
        return 1;
    }

    @Override
    public int[] onSave() {
        int[] data = new int[4 + this.drawCounts.length];
        data[0] = this.xp;
        data[1] = this.currentState.ordinal();
        data[2] = this.battlesToNextTribulation;
        data[3] = this.completedTribulationIndex;
        for (int i = 0; i < this.drawCounts.length; i++) {
            data[4 + i] = this.drawCounts[i];
        }
        return data;
    }

    @Override
    public void onLoad(int[] savedData) {
        if (savedData != null && savedData.length >= 1) {
            this.xp = savedData[0];
            if (savedData.length >= 2) {
                int stateIndex = savedData[1];
                if (stateIndex >= 0 && stateIndex < KongQiaoState.values().length) {
                    this.currentState = KongQiaoState.values()[stateIndex];
                }
            }
            if (savedData.length >= 3) {
                this.battlesToNextTribulation = savedData[2];
            }

            int offset = 3;
            if (savedData.length >= 4 + this.drawCounts.length) {
                this.completedTribulationIndex = savedData[3];
                offset = 4;
            } else {
                this.completedTribulationIndex = -1;
            }

            for (int i = 0; i < this.drawCounts.length && (i + offset) < savedData.length; i++) {
                this.drawCounts[i] = savedData[i + offset];
            }
            updateDescription();
        }
        updatePulseStatus();
    }

    @Override
    public Type savedType() {
        return int[].class;
    }
}