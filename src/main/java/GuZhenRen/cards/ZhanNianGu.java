package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import GuZhenRen.powers.QingPower;
import GuZhenRen.powers.ZhiDaoDaoHenPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class ZhanNianGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ZhanNianGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhanNianGu.png");

    private static final int COST = 0;
    private static final int INITIAL_RANK = 2; // 2转

    public ZhanNianGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        // magicNumber 用于控制【念】（基础3层）
        this.baseMagicNumber = this.magicNumber = 3;

        // secondMagicNumber 用于控制【力量】（基础2点）
        this.baseSecondMagicNumber = this.secondMagicNumber = 2;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void applyPowers() {
        this.magicNumber = this.baseMagicNumber;
        super.applyPowers();

        int bonus = 0;

        // 1. 计算【情】的加成
        if (AbstractDungeon.player.hasPower(QingPower.POWER_ID)) {
            bonus += AbstractDungeon.player.getPower(QingPower.POWER_ID).amount / 3;
        }

        // 2. 计算【智道道痕】的加成
        if (AbstractDungeon.player.hasPower(ZhiDaoDaoHenPower.POWER_ID)) {
            bonus += AbstractDungeon.player.getPower(ZhiDaoDaoHenPower.POWER_ID).amount / 3;
        }

        if (bonus > 0) {
            this.magicNumber += bonus;
            this.isMagicNumberModified = true;
        }
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        // 先检测原版的基础打出条件（如费用是否足够、是否处于虚弱等）
        boolean canUse = super.canUse(p, m);
        if (!canUse) {
            return false;
        }

        // 遍历手牌，统计攻击牌数量
        int attackCount = 0;
        for (AbstractCard c : p.hand.group) {
            if (c.type == CardType.ATTACK) {
                attackCount++;
            }
        }

        // 判断条件
        if (attackCount < 4) {
            this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
            return false;
        }

        return true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得力量
        this.addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.secondMagicNumber), this.secondMagicNumber));

        // 获得念
        this.addToBot(new ApplyPowerAction(p, p, new NianPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();

            // 升级后力量增加 (2 -> 3)
            this.upgradeSecondMagicNumber(1);

            // 升级转数 (2转 -> 3转)
            this.upgradeRank(1);

            this.initializeDescription();
        }
    }
}