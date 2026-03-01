package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.powers.JianHenPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class LangJian extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LangJian");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LangJian.png");

    private static final int COST = 0;
    private static final int DAMAGE = 3;             // 基础伤害 3
    private static final int UPGRADE_PLUS_DMG = 1;   // 升级加 1 点，变为 4
    private static final int MAGIC = 1;              // 给予的剑痕层数
    private static final int UPGRADE_MAGIC = 1;      // 升级后变 2 层
    private static final int INITIAL_RANK = 6;       // 6转起步

    public LangJian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.ENEMY);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);
        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

        // 2. 给予剑痕
        this.addToBot(new ApplyPowerAction(m, p, new JianHenPower(m, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DMG);
            this.upgradeMagicNumber(UPGRADE_MAGIC);  // 剑痕 1 -> 2
            this.upgradeRank(1);                     // 6转 -> 7转
            this.initializeDescription();
        }
    }


    // 使用内部补丁监听全局出牌，实现弃牌堆精准回手
    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class LangJianReturnPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {

            // 1. 如果玩家打出的是剑道牌，并且不是浪剑自己
            if (c.hasTag(GuZhenRenTags.JIAN_DAO) && !c.cardID.equals(LangJian.ID)) {

                // 2. 遍历玩家的弃牌堆
                for (AbstractCard card : __instance.discardPile.group) {

                    // 3. 如果在弃牌堆里找到了浪剑
                    if (card.cardID.equals(LangJian.ID)) {

                        // 排入回手动作
                        AbstractDungeon.actionManager.addToBottom(new DiscardToHandAction(card));
                    }
                }
            }
        }
    }
}