package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import GuZhenRen.powers.QuanLiYiFuPower;
import GuZhenRen.powers.LiDaoDaoHenPower; // 【新增】导入力道道痕

public class ZiLiGengShengGu extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("ZiLiGengShengGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZiLiGengShengGu.png");

    private static final int COST = 1;
    private static final int BASE_HEAL = 4;

    private static final int INITIAL_RANK = 3; // 初始3转

    public ZiLiGengShengGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.baseMagicNumber = BASE_HEAL;
        this.magicNumber = this.baseMagicNumber;

        this.exhaust = true;

        this.setDao(Dao.LI_DAO);


        this.setRank(INITIAL_RANK);
        this.tags.add(CardTags.HEALING);
    }

    /**
     * 辅助方法：计算当前的总力量加成
     * 包含：力量 + 力道道痕，并受全力以赴加成
     */
    private int calculateTotalStrength(AbstractPlayer p) {
        int totalStrength = 0;

        // 1. 力量
        if (p.hasPower(StrengthPower.POWER_ID)) {
            totalStrength += p.getPower(StrengthPower.POWER_ID).amount;
        }

        // 2. 力道道痕
        if (p.hasPower(LiDaoDaoHenPower.POWER_ID)) {
            totalStrength += p.getPower(LiDaoDaoHenPower.POWER_ID).amount;
        }

        // 3. 全力以赴 (加成翻倍)
        if (p.hasPower(QuanLiYiFuPower.POWER_ID)) {
            totalStrength *= 2;
        }

        return totalStrength;
    }

    @Override
    public void applyPowers() {
        // 1. 重置基础值
        this.magicNumber = this.baseMagicNumber;

        AbstractPlayer p = AbstractDungeon.player;
        if (p != null) {
            // 2. 获取计算后的总力量加成
            int bonus = calculateTotalStrength(p);

            // 3. 应用加成
            this.magicNumber += bonus;

            if (this.magicNumber != this.baseMagicNumber) {
                this.isMagicNumberModified = true;
            }
        }

        super.applyPowers();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 在 use 中也调用一次计算逻辑，确保数值与牌面显示一致
        int bonus = calculateTotalStrength(p);
        int finalHeal = this.baseMagicNumber + bonus;

        this.addToBot(new HealAction(p, p, finalHeal));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(0);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}