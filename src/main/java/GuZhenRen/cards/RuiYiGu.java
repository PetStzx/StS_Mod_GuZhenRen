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
    private static final int MAGIC = 2; // 基础 2 层剑痕，2 层意
    private static final int UPGRADE_PLUS_MAGIC = 1; // 升级后变 3 层
    private static final int INITIAL_RANK = 3; // 3转起步

    public RuiYiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON, // 蓝卡
                CardTarget.ALL_ENEMY); // 虽然有针对性，但依然属于全场扫描牌

        this.setDao(Dao.ZHI_DAO);
        this.setRank(INITIAL_RANK);
        this.baseMagicNumber = this.magicNumber = MAGIC;

        this.exhaust = true; // 消耗
    }

    private boolean isAttacking(AbstractMonster m) {
        return m.intent == AbstractMonster.Intent.ATTACK ||
                m.intent == AbstractMonster.Intent.ATTACK_BUFF ||
                m.intent == AbstractMonster.Intent.ATTACK_DEBUFF ||
                m.intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得 1 层剑锋
        this.addToBot(new ApplyPowerAction(p, p, new JianFengPower(p, 1), 1));

        int attackingEnemies = 0;

        // 2. 遍历所有敌人，寻找准备攻击的敌人
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                // 判断意图是否包含攻击
                if (isAttacking(mo)) {
                    // 只给有攻击意图的敌人挂剑痕
                    this.addToBot(new ApplyPowerAction(mo, p, new JianHenPower(mo, this.magicNumber), this.magicNumber));
                    attackingEnemies++;
                }
            }
        }

        // 3. 根据准备攻击的敌人数量，获得对应层数的意
        if (attackingEnemies > 0) {
            int totalYi = attackingEnemies * this.magicNumber;
            this.addToBot(new ApplyPowerAction(p, p, new YiPower(p, totalYi), totalYi));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC); // 2 -> 3
            this.upgradeRank(1); // 3转 -> 4转
            this.initializeDescription();
        }
    }
}