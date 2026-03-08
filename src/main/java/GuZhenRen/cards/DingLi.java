package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class DingLi extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("DingLi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/DingLi.png");

    private static final int COST = 2;
    private static final int BASE_BLOCK = 14;

    // 力量的倍数乘区
    private static final int STRENGTH_MULTIPLIER = 3;

    private static final int INITIAL_RANK = 6; // 6转

    public DingLi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.SELF);

        this.baseBlock = BASE_BLOCK;
        this.baseMagicNumber = this.magicNumber = STRENGTH_MULTIPLIER;

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);
    }

    // =========================================================================
    // 带入力量乘区
    // =========================================================================
    @Override
    public void applyPowers() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            int extraBlock = 0;
            if (p.hasPower(StrengthPower.POWER_ID)) {
                // 力量值 乘以 魔法值(3)
                extraBlock = p.getPower(StrengthPower.POWER_ID).amount * this.magicNumber;
            }

            int realBaseBlock = this.baseBlock;

            // 把力量倍数收益临时垫进 baseBlock 里
            this.baseBlock += extraBlock;

            // 让引擎去计算敏捷、脆弱等 Buff 的加成
            super.applyPowers();

            // 计算完之后，把真实的 baseBlock 换回来
            this.baseBlock = realBaseBlock;

            // 如果最终结果不等于基础值，面板上的数值会变色
            this.isBlockModified = (this.block != this.baseBlock);
        } else {
            super.applyPowers();
        }
    }

    // 打出卡牌瞬间的数值最终结算
    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            int extraBlock = 0;
            if (p.hasPower(StrengthPower.POWER_ID)) {
                // 力量值 乘以 魔法值(3)
                extraBlock = p.getPower(StrengthPower.POWER_ID).amount * this.magicNumber;
            }

            int realBaseBlock = this.baseBlock;

            // 把力量倍数收益临时垫进 baseBlock 里
            this.baseBlock += extraBlock;

            // 调用父类的结算方法，它内部会重新计算敏捷等属性
            super.calculateCardDamage(mo);

            // 计算完之后，把真实的 baseBlock 换回来
            this.baseBlock = realBaseBlock;

            this.isBlockModified = (this.block != this.baseBlock);
        } else {
            super.calculateCardDamage(mo);
        }
    }

    // =========================================================================
    // 打出结算
    // =========================================================================
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.retain = true; // 升级后获得保留属性
            this.upgradeRank(1); // 6转 -> 7转
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}