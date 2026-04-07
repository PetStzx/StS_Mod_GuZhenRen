package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.patches.GuZhenRenTags;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
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
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.lang.reflect.Type;

public abstract class AbstractKongQiao extends CustomRelic implements CustomSavable<Integer> {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("KongQiaoUI"));
    public static final String[] TEXT = uiStrings.TEXT;

    private static final UIStrings globalStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("CardGlobalText"));
    public static final String[] GLOBAL_TEXT = globalStrings.TEXT;

    public int xp = 0;
    public int rank = 1;
    protected int neededXP = 1;
    protected String nextRelicID = "";

    public boolean effectUsedThisCombat = false;

    public AbstractKongQiao(String id, String imgName, RelicTier tier, LandingSound sound) {
        super(
                id,
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + imgName)),
                ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/outline/" + imgName)),
                tier,
                sound
        );
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
    public void atPreBattle() {
        this.effectUsedThisCombat = false;
        if (this.rank > 1) {
            this.beginLongPulse();
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!this.effectUsedThisCombat && this.rank > 1) {
            if (card instanceof AbstractGuZhenRenCard) {
                int cardRank = ((AbstractGuZhenRenCard) card).rank;
                // 判定：转数大于等于1，且严格低于当前空窍转数
                if (cardRank >= 1 && cardRank < this.rank) {
                    this.effectUsedThisCombat = true; // 标记本场战斗已使用
                    this.flash();
                    this.stopPulse();

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

    @Override
    public String getUpdatedDescription() {
        if (this.DESCRIPTIONS == null || this.DESCRIPTIONS.length == 0) {
            return "";
        }

        String baseDesc = this.DESCRIPTIONS[0];
        String xpDesc;

        if (nextRelicID == null || nextRelicID.isEmpty()) {
            xpDesc = "";
        } else {
            int remaining = neededXP - xp;
            if (remaining < 0) remaining = 0;
            xpDesc = String.format(TEXT[0], remaining);
        }

        return baseDesc + xpDesc;
    }

    public void updateDescription() {
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));

        addKeywordTip(TEXT[1]);

        if (this.rank > 1) {
            String rankKeyword = getRankKeywordName(this.rank);
            addKeywordTip(rankKeyword);
        }
    }

    private void addKeywordTip(String keywordName) {
        if (keywordName == null) return;

        String description = null;

        if (GameDictionary.keywords.containsKey(keywordName)) {
            description = GameDictionary.keywords.get(keywordName);
        }
        else if (GameDictionary.keywords.containsKey("guzhenren:" + keywordName)) {
            description = GameDictionary.keywords.get("guzhenren:" + keywordName);
        }
        else if (GameDictionary.keywords.containsKey(keywordName.toLowerCase())) {
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

    public void gainXP(int amount) {
        if (nextRelicID == null || nextRelicID.isEmpty()) return;
        this.xp += amount;
        this.flash();
        if (this.xp >= this.neededXP) {
            int overflowXP = this.xp - this.neededXP;
            evolve(overflowXP);
        } else {
            updateDescription();
        }
    }

    private void evolve(int overflowXP) {
        GuZhenRen.logger.info("空窍升级。目标: " + this.nextRelicID);

        int relicIndex = AbstractDungeon.player.relics.indexOf(this);
        AbstractRelic newRelic = RelicLibrary.getRelic(this.nextRelicID).makeCopy();

        if (newRelic instanceof AbstractKongQiao) {
            AbstractKongQiao nextKongQiao = (AbstractKongQiao) newRelic;
            nextKongQiao.xp = overflowXP;
            nextKongQiao.updateDescription();
            autoUpgradeVitalGu(nextKongQiao.rank);
        }

        newRelic.instantObtain(AbstractDungeon.player, relicIndex, true);
        newRelic.flash();
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

    @Override
    public void onVictory() {
        this.stopPulse();
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room instanceof MonsterRoomBoss) gainXP(4);
        else if (room instanceof MonsterRoomElite) gainXP(2);
        else gainXP(1);
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
    public Integer onSave() {
        return this.xp;
    }

    @Override
    public void onLoad(Integer savedXp) {
        if (savedXp != null) {
            this.xp = savedXp;
            updateDescription();
        }
    }

    @Override
    public Type savedType() {
        return Integer.class;
    }
}