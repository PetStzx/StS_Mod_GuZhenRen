package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.relics.AbstractKongQiao;
import GuZhenRen.patches.GuZhenRenTags;
import basemod.abstracts.CustomCard;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.SpawnModificationCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.lang.reflect.Type;
import java.util.ArrayList;

public abstract class AbstractGuZhenRenCard extends CustomCard implements CustomSavable<int[]>, SpawnModificationCard {

    // =========================================================================
    //  本地化资源加载
    // =========================================================================
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("CardGlobalText"));
    public static final String[] TEXT = uiStrings.TEXT;

    private static final int DAO_TEXT_START_INDEX = 10;

    public enum Dao {
        GUANG_DAO, // 10 光道
        YAN_DAO,   // 11 炎道
        LI_DAO,    // 12 力道
        JIN_DAO,   // 13 金道
        TOU_DAO,   // 14 偷道
        MU_DAO,    // 15 木道
        SHI_DAO,   // 16 食道
        SHA_DAO,   // 17 杀道
        GU_DAO,    // 18 骨道
        LU_DAO,     // 19 律道
        ZHI_DAO,    // 20 智道
        BIAN_HUA_DAO,       // 21 变化道
        YIN_YANG_DAO,      // 22 阴阳道
        JIAN_DAO      // 23 剑道

    }

    // =========================================================================
    //  卡牌基础属性
    // =========================================================================
    public int baseRank = 0;
    public int rank = 0;
    public boolean upgradedRank = false;
    public String myBaseDescription = "";
    public String guPathString = "";
    public boolean isRankLocked = false;

    // 第二魔法值 (用于显示特殊数值)
    public int secondMagicNumber = -1;
    public int baseSecondMagicNumber = -1;
    public boolean upgradedSecondMagicNumber = false;
    public boolean isSecondMagicNumberModified = false;

    public AbstractGuZhenRenCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
        this.myBaseDescription = rawDescription;
    }

    // =========================================================================
    //  【核心方法】设置流派
    // =========================================================================
    protected void setDao(Dao dao) {
        switch (dao) {
            case GUANG_DAO: this.tags.add(GuZhenRenTags.GUANG_DAO); break;
            case YAN_DAO:   this.tags.add(GuZhenRenTags.YAN_DAO); break;
            case LI_DAO:    this.tags.add(GuZhenRenTags.LI_DAO); break;
            case JIN_DAO:   this.tags.add(GuZhenRenTags.JIN_DAO); break;
            case TOU_DAO:   this.tags.add(GuZhenRenTags.TOU_DAO); break;
            case MU_DAO:    this.tags.add(GuZhenRenTags.MU_DAO); break;
            case SHI_DAO:   this.tags.add(GuZhenRenTags.SHI_DAO); break;
            case SHA_DAO:   this.tags.add(GuZhenRenTags.SHA_DAO); break;
            case GU_DAO:    this.tags.add(GuZhenRenTags.GU_DAO); break;
            case LU_DAO:    this.tags.add(GuZhenRenTags.LU_DAO); break;
            case ZHI_DAO:    this.tags.add(GuZhenRenTags.ZHI_DAO); break;
            case BIAN_HUA_DAO:    this.tags.add(GuZhenRenTags.BIAN_HUA_DAO); break;
            case YIN_YANG_DAO:    this.tags.add(GuZhenRenTags.YIN_YANG_DAO); break;
            case JIAN_DAO:    this.tags.add(GuZhenRenTags.JIAN_DAO); break;
        }

        int index = DAO_TEXT_START_INDEX + dao.ordinal();

        if (index < TEXT.length) {
            this.guPathString = "guzhenren:" + TEXT[index];
        } else {
            this.guPathString = "";
        }

        initializeDescription();
    }

    // =========================================================================
    //  applyPowers 防崩溃与逻辑处理
    // =========================================================================
    @Override
    public void applyPowers() {
        if (AbstractDungeon.player == null) {
            initializeDescription();
            return;
        }
        applyRankLock();
        super.applyPowers();
        initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        applyRankLock();
        super.calculateCardDamage(mo);
        initializeDescription();
    }

    public void applyRankLock() {
        if (this.isRankLocked) return;
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) return;
        if (AbstractDungeon.player.masterDeck.contains(this)) return;
        if (AbstractDungeon.getCurrRoom() == null || AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) return;

        boolean inCombatGroup = AbstractDungeon.player.hand.contains(this) ||
                AbstractDungeon.player.drawPile.contains(this) ||
                AbstractDungeon.player.discardPile.contains(this) ||
                AbstractDungeon.player.limbo.contains(this) ||
                AbstractDungeon.player.exhaustPile.contains(this);

        if (!inCombatGroup) return;

        int playerRank = getPlayerApertureRank();

        if (this.rank > playerRank) {
            int diff = this.rank - playerRank;
            if (this.cost >= 0) {
                int oldCost = this.cost;
                this.cost += diff;
                if (this.costForTurn == oldCost && !this.isCostModifiedForTurn) {
                    this.costForTurn = this.cost;
                }
                this.isCostModified = true;
            }
        }
        this.isRankLocked = true;
    }

    public int getPlayerApertureRank() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return 1;
        for (AbstractRelic r : p.relics) {
            if (r instanceof AbstractKongQiao) {
                return ((AbstractKongQiao) r).rank;
            }
        }
        return 1;
    }

    protected void setRank(int amount) {
        this.baseRank = amount;
        this.rank = amount;
        initializeDescription();
    }

    protected void upgradeRank(int amount) {
        this.baseRank += amount;
        this.rank = this.baseRank;
        this.upgradedRank = true;
        initializeDescription();
    }

    public void updateRankDescription() {
        initializeDescription();
    }

    @Override
    public void initializeDescription() {
        this.rawDescription = constructRawDescription();
        super.initializeDescription();
    }

    public boolean isXianGu() {
        return this.tags.contains(GuZhenRenTags.XIAN_GU) || this.rank >= 6;
    }

    // =========================================================================
    //  描述构建逻辑 (支持本地化)
    // =========================================================================
    protected String constructRawDescription() {
        if (this.myBaseDescription == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        String rankKeyword = getRankKeywordText(this.rank);
        sb.append("guzhenren:").append(rankKeyword);

        String separator = (TEXT.length > 9) ? TEXT[9] : " 。 ";

        if (guPathString != null && !guPathString.isEmpty()) {
            sb.append(separator).append(guPathString).append(separator);
        }

        boolean isXianGu = this.isXianGu();

        if (this.tags.contains(GuZhenRenTags.BEN_MING_GU)) {
            sb.append(" guzhenren:本命蛊 ").append(separator);
        } else if (isXianGu) {
            if (!this.tags.contains(GuZhenRenTags.XIAN_GU)) {
                this.tags.add(GuZhenRenTags.XIAN_GU);
            }
            sb.append(" guzhenren:仙蛊 ").append(separator);
        } else {
            this.tags.remove(GuZhenRenTags.XIAN_GU);
        }

        sb.append(" NL ").append(this.myBaseDescription);
        return sb.toString();
    }

    private String getRankKeywordText(int r) {
        if (r >= 1 && r <= 9) {
            return TEXT[r - 1];
        }
        return TEXT[0];
    }

    public void upgradeSecondMagicNumber(int amount) {
        this.baseSecondMagicNumber += amount;
        this.secondMagicNumber = this.baseSecondMagicNumber;
        this.upgradedSecondMagicNumber = true;
    }

    // 2. 重写原版的升级预览渲染方法
    @Override
    public void displayUpgrades() {
        super.displayUpgrades();
        if (this.upgradedSecondMagicNumber) {
            this.secondMagicNumber = this.baseSecondMagicNumber;
            this.isSecondMagicNumberModified = true;
        }
    }

    @Override
    public boolean canSpawn(ArrayList<AbstractCard> currentRewardCards) {
        boolean isXianGu = this.tags.contains(GuZhenRenTags.XIAN_GU) || this.rank >= 6;
        if (isXianGu) {
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.cardID.equals(this.cardID)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRewardListCreated(ArrayList<AbstractCard> rewardCards) {
    }

    // =========================================================================
    //  存档/读档逻辑
    // =========================================================================
    @Override
    public int[] onSave() {
        return new int[]{this.rank, this.misc};
    }

    @Override
    public void onLoad(int[] savedData) {
        if (savedData != null && savedData.length >= 1) {
            this.rank = savedData[0];
            this.baseRank = savedData[0];
            if (savedData.length >= 2) {
                this.misc = savedData[1];
            }

            // 触发读档后的数据重算钩子
            this.onRankLoaded();

            // 仅更新描述文本即可，数值计算留给游戏正常流程
            this.initializeDescription();
        }
    }

    // 【新增钩子方法】供子类（如本命蛊）重写，用于在读档后立刻刷新数值
    protected void onRankLoaded() {
        // 默认留空，由需要的子类去重写
    }

    @Override
    public Type savedType() {
        return int[].class;
    }

    @Override
    public AbstractGuZhenRenCard makeStatEquivalentCopy() {
        AbstractGuZhenRenCard c = (AbstractGuZhenRenCard) super.makeStatEquivalentCopy();
        c.baseRank = this.baseRank;
        c.rank = this.rank;
        c.myBaseDescription = this.myBaseDescription;
        c.guPathString = this.guPathString;
        c.isRankLocked = this.isRankLocked;

        // 复制时也触发一次钩子，确保克隆出来的牌（比如在牌组查看界面）数值正确
        c.onRankLoaded();

        c.initializeDescription();
        return c;
    }
}