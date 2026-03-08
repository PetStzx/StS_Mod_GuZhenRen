package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.relics.AbstractKongQiao;
import GuZhenRen.relics.YanXinGu;
import GuZhenRen.powers.YanDaoDaoHenPower;
import GuZhenRen.powers.QingPower; // 导入情能力
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

    private static final UIStrings tagStrings = CardCrawlGame.languagePack.getUIString(GuZhenRen.makeID("CardTags"));
    public static final String[] TAG_TEXT = tagStrings.TEXT;

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
        JIAN_DAO,      // 23 剑道
        XUE_DAO,      // 24 血道
        YUN_DAO,      // 25 运道
        FENG_DAO,      // 26 风道
        ZHOU_DAO,      // 27 宙道

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

    // =========================================================================
    // 模组专属底层属性：焚烧 (FenShao)
    // =========================================================================
    public int baseFenShao = -1; // -1表示该牌不使用焚烧机制
    public int fenShao = -1;
    public boolean isFenShaoModified = false;
    public boolean upgradedFenShao = false;

    // =========================================================================
    // 模组专属底层属性：念 (Nian)
    // =========================================================================
    public int baseNian = -1; // -1表示该牌不使用念机制
    public int nian = -1;
    public boolean isNianModified = false;
    public boolean upgradedNian = false;

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
            case XUE_DAO:    this.tags.add(GuZhenRenTags.XUE_DAO); break;
            case YUN_DAO:    this.tags.add(GuZhenRenTags.YUN_DAO); break;
            case FENG_DAO:    this.tags.add(GuZhenRenTags.FENG_DAO); break;
            case ZHOU_DAO:    this.tags.add(GuZhenRenTags.ZHOU_DAO); break;



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
    //  底层运算逻辑
    // =========================================================================
    private void calculateFenShaoBonus() {
        if (this.baseFenShao > -1) {
            this.fenShao = this.baseFenShao;

            if (AbstractDungeon.player != null) {
                // 1. 道痕加成
                if (AbstractDungeon.player.hasPower(YanDaoDaoHenPower.POWER_ID)) {
                    this.fenShao += AbstractDungeon.player.getPower(YanDaoDaoHenPower.POWER_ID).amount / 2;
                }

                // 2. 炎心蛊遗物加成
                if (AbstractDungeon.player.hasRelic(YanXinGu.ID)) {
                    this.fenShao += 1; // 拥有炎心蛊，焚烧层数 +1
                }
            }

            this.isFenShaoModified = (this.fenShao != this.baseFenShao);
        }
    }

    private void calculateNianBonus() {
        if (this.baseNian > -1) {
            this.nian = this.baseNian;

            if (AbstractDungeon.player != null) {
                // 智道“情”能力加成
                if (AbstractDungeon.player.hasPower(QingPower.POWER_ID)) {
                    this.nian += AbstractDungeon.player.getPower(QingPower.POWER_ID).amount / 3;
                }
            }

            this.isNianModified = (this.nian != this.baseNian);
        }
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
        calculateFenShaoBonus(); // 计算焚烧加成
        calculateNianBonus();    // 计算念的加成
        initializeDescription();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        applyRankLock();
        super.calculateCardDamage(mo);
        calculateFenShaoBonus(); // 计算焚烧加成
        calculateNianBonus();    // 计算念的加成
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
        this.onRankLoaded();
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

        String separator = (TEXT.length > 9) ? TEXT[9] : " . ";

        if (guPathString != null && !guPathString.isEmpty()) {
            sb.append(separator).append(guPathString).append(separator);
        }

        boolean isXianGu = this.isXianGu();

        if (this.tags.contains(GuZhenRenTags.BEN_MING_GU)) {
            sb.append(" guzhenren:").append(TAG_TEXT[0]).append(" ").append(separator);
        } else if (isXianGu) {
            if (!this.tags.contains(GuZhenRenTags.XIAN_GU)) {
                this.tags.add(GuZhenRenTags.XIAN_GU);
            }
            sb.append(" guzhenren:").append(TAG_TEXT[1]).append(" ").append(separator);
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

    // 焚烧升级方法
    public void upgradeFenShao(int amount) {
        this.baseFenShao += amount;
        this.fenShao = this.baseFenShao;
        this.upgradedFenShao = true;
    }

    // 念升级方法
    public void upgradeNian(int amount) {
        this.baseNian += amount;
        this.nian = this.baseNian;
        this.upgradedNian = true;
    }

    // 2. 重写原版的升级预览渲染方法
    @Override
    public void displayUpgrades() {
        super.displayUpgrades();
        if (this.upgradedSecondMagicNumber) {
            this.secondMagicNumber = this.baseSecondMagicNumber;
            this.isSecondMagicNumberModified = true;
        }
        if (this.upgradedFenShao) {
            this.fenShao = this.baseFenShao;
            this.isFenShaoModified = true;
        }
        if (this.upgradedNian) {
            this.nian = this.baseNian;
            this.isNianModified = true;
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

            // 更新描述文本
            this.initializeDescription();
        }
    }

    // 供子类（如本命蛊）重写，用于在读档后立刻刷新数值
    protected void onRankLoaded() {
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

        // 同步专属的底层变量状态
        c.baseFenShao = this.baseFenShao;
        c.fenShao = this.fenShao;
        c.isFenShaoModified = this.isFenShaoModified;
        c.upgradedFenShao = this.upgradedFenShao;

        c.baseSecondMagicNumber = this.baseSecondMagicNumber;
        c.secondMagicNumber = this.secondMagicNumber;
        c.isSecondMagicNumberModified = this.isSecondMagicNumberModified;
        c.upgradedSecondMagicNumber = this.upgradedSecondMagicNumber;

        c.baseNian = this.baseNian;
        c.nian = this.nian;
        c.isNianModified = this.isNianModified;
        c.upgradedNian = this.upgradedNian;

        // 继承虚影标签，用于仙蛊唯一豁免
        if (this.tags.contains(GuZhenRenTags.XU_YING_COPY)) {
            c.tags.add(GuZhenRenTags.XU_YING_COPY);
        }

        // 复制时也触发一次钩子，确保克隆出来的牌数值正确
        c.onRankLoaded();

        c.initializeDescription();
        return c;
    }
}