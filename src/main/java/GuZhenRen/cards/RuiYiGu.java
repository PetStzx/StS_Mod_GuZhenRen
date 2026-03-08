package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.JianFengPower;
import GuZhenRen.powers.JianHenPower;
import GuZhenRen.powers.YiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class RuiYiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("RuiYiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/RuiYiGu.png");

    private static final int COST = 1;

    // MagicNumber 用于“意”的层数
    private static final int MAGIC = 2;
    private static final int UPGRADE_PLUS_MAGIC = 1; // 意：2 -> 3

    // SecondMagicNumber 用于“剑痕”的层数
    private static final int SECOND_MAGIC = 4;
    private static final int UPGRADE_PLUS_SECOND_MAGIC = 2; // 剑痕：4 -> 6

    private static final int INITIAL_RANK = 3;

    public RuiYiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.ZHI_DAO);
        this.setRank(INITIAL_RANK);

        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.baseSecondMagicNumber = this.secondMagicNumber = SECOND_MAGIC;

    }

    private boolean isAttacking(AbstractMonster m) {
        return m.intent == AbstractMonster.Intent.ATTACK ||
                m.intent == AbstractMonster.Intent.ATTACK_BUFF ||
                m.intent == AbstractMonster.Intent.ATTACK_DEBUFF ||
                m.intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        boolean allAttacking = true;
        boolean hasAliveEnemies = false;

        // 1. 遍历所有敌人，给予剑痕并判断是否全员攻击
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                hasAliveEnemies = true;

                if (isAttacking(mo)) {
                    // 只要有攻击意图，就给剑痕
                    this.addToBot(new ApplyPowerAction(mo, p, new JianHenPower(mo, this.secondMagicNumber), this.secondMagicNumber));
                } else {
                    // 发现任何一个没有攻击意图的存活敌人，判定失败
                    allAttacking = false;
                }
            }
        }

        // 2. 若场上有存活敌人，且所有存活敌人的意图均为攻击，则获得剑锋和意
        if (hasAliveEnemies && allAttacking) {
            this.addToBot(new ApplyPowerAction(p, p, new JianFengPower(p, 1), 1));
            this.addToBot(new ApplyPowerAction(p, p, new YiPower(p, this.magicNumber), this.magicNumber));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeSecondMagicNumber(UPGRADE_PLUS_SECOND_MAGIC);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}