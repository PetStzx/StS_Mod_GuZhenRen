package GuZhenRen.relics;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractGuZhenRenCard;
import GuZhenRen.patches.GuZhenRenTags;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary; // 必须导入
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;      // 必须导入
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.TipHelper;     // 必须导入
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.lang.reflect.Type;

public abstract class AbstractKongQiao extends CustomRelic implements CustomSavable<Integer> {

    public int xp = 0;
    public int rank = 1;
    protected int neededXP = 1;
    protected String nextRelicID = "";

    public AbstractKongQiao(String id, String imgName, RelicTier tier, LandingSound sound) {
        super(id, ImageMaster.loadImage(GuZhenRen.assetPath("img/relics/" + imgName)), tier, sound);
    }

    protected void initStats(int rank, int neededXP, String nextRelicID) {
        this.rank = rank;
        this.neededXP = neededXP;
        this.nextRelicID = nextRelicID;
        this.counter = this.rank;
        updateDescription();
    }

    // 1. 获取完整的动态文本
    @Override
    public String getUpdatedDescription() {
        if (this.DESCRIPTIONS == null || this.DESCRIPTIONS.length == 0) {
            return "";
        }

        String baseDesc = this.DESCRIPTIONS[0];
        String xpDesc;

        if (nextRelicID == null || nextRelicID.isEmpty()) {
            xpDesc = " NL 已达巅峰！";
        } else {
            int remaining = neededXP - xp;
            if (remaining < 0) remaining = 0;
            // 这里的 #y修为 只是为了让正文变黄，右侧提示框由下面的 updateDescription 手动添加
            xpDesc = " NL 还需 #b" + remaining + " 点 #y修为 晋升。";
        }

        return baseDesc + xpDesc;
    }

    // 2. 【核心修复】完全手动构建提示框列表，不调用 initializeTips()
    public void updateDescription() {
        // 更新描述字符串
        this.description = getUpdatedDescription();

        // 清空旧提示
        this.tips.clear();

        // A. 添加主提示（遗物自己的名字和描述）
        this.tips.add(new PowerTip(this.name, this.description));

        // B. 手动添加 "修为" 关键词
        addKeywordTip("修为");

        // C. 手动添加 "一转/二转..." 关键词
        String rankKeyword = getRankKeywordName(this.rank);
        addKeywordTip(rankKeyword);
    }

    // 【新增辅助方法】安全添加关键词提示
    private void addKeywordTip(String keywordName) {
        if (keywordName == null) return;

        String description = null;

        // 尝试直接查找 (例如 "修为")
        if (GameDictionary.keywords.containsKey(keywordName)) {
            description = GameDictionary.keywords.get(keywordName);
        }
        // 尝试加前缀查找 (例如 "guzhenren:修为")，防止 BaseMod 注册时加了前缀
        else if (GameDictionary.keywords.containsKey("guzhenren:" + keywordName)) {
            description = GameDictionary.keywords.get("guzhenren:" + keywordName);
            // 如果原来的名字查不到，可能是内部名为 guzhenren:xxx，但显示名还是得用 keywordName
        }
        // 尝试全小写查找 (容错)
        else if (GameDictionary.keywords.containsKey(keywordName.toLowerCase())) {
            description = GameDictionary.keywords.get(keywordName.toLowerCase());
        }

        // 如果找到了描述，就添加提示
        if (description != null) {
            this.tips.add(new PowerTip(TipHelper.capitalize(keywordName), description));
        }
    }

    private String getRankKeywordName(int r) {
        switch (r) {
            case 1: return "一转";
            case 2: return "二转";
            case 3: return "三转";
            case 4: return "四转";
            case 5: return "五转";
            case 6: return "六转";
            case 7: return "七转";
            case 8: return "八转";
            case 9: return "九转";
            default: return "一转";
        }
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
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room instanceof MonsterRoomBoss) gainXP(4);
        else if (room instanceof MonsterRoomElite) gainXP(3);
        else gainXP(2);
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