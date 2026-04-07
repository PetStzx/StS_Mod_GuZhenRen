package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LongXingHuBuGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LongXingHuBuGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LongXingHuBuGu.png");

    private static final int COST = 0;
    private static final int BLOCK = 4;
    private static final int UPGRADE_PLUS_BLOCK = 3; // 升级加 3 点，变为 7
    private static final int INITIAL_RANK = 4;       // 4转

    public LongXingHuBuGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.SELF);

        this.setDao(Dao.LI_DAO);

        this.baseBlock = this.block = BLOCK;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_PLUS_BLOCK);
            this.upgradeRank(1); // 4转 -> 5转
            this.initializeDescription();
        }
    }

    // =========================================================================
    // 终极解决方案：精准拦截力量的获得
    // =========================================================================

    // 补丁 1：监听已有力量时的层数叠加 (【核心修复】：精准定位到 StrengthPower 类)
    @SpirePatch(clz = StrengthPower.class, method = "stackPower")
    public static class StackPowerReturnPatch {
        @SpirePostfixPatch
        public static void Postfix(StrengthPower __instance, int stackAmount) {
            // 判断：状态属于玩家，且增加量 > 0 (排除被上虚弱掉力量的情况)
            if (__instance.owner != null && __instance.owner.isPlayer && stackAmount > 0) {
                checkAndReturnFromDiscard();
            }
        }
    }

    // 补丁 2：监听本场战斗第一次获得力量 (StrengthPower 没有重写此方法，所以继续监听 AbstractPower)
    @SpirePatch(clz = AbstractPower.class, method = "onInitialApplication")
    public static class InitialPowerReturnPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPower __instance) {
            // 判断：状态属于玩家，且类型是力量，且初始获得量 > 0
            if (__instance.owner != null && __instance.owner.isPlayer && __instance.ID.equals(StrengthPower.POWER_ID) && __instance.amount > 0) {
                checkAndReturnFromDiscard();
            }
        }
    }

    // 触发回手的通用方法
    private static void checkAndReturnFromDiscard() {
        if (AbstractDungeon.player == null) return;

        // 遍历玩家当前的弃牌堆
        for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
            // 只要找到龙行虎步蛊，就立刻排入回手动作
            if (card.cardID.equals(LongXingHuBuGu.ID)) {
                AbstractDungeon.actionManager.addToBottom(new DiscardToHandAction(card));
            }
        }
    }
}