package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ShiZhen extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ShiZhen");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ShiZhen.png");

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPGRADE_PLUS_DAMAGE = 1; // 升级基础伤害 6 -> 7
    private static final int MAGIC = 2;
    private static final int UPGRADE_PLUS_MAGIC = 1;  // 升级额外伤害 2 -> 3
    private static final int INITIAL_RANK = 6;

    public ShiZhen() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.ZHOU_DAO);
        this.setRank(INITIAL_RANK);

        this.baseDamage = this.damage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    // 获取卡牌初始的纯净基础伤害（判断是否升级）
    private int getOriginalBaseDamage() {
        return this.upgraded ? (DAMAGE + UPGRADE_PLUS_DAMAGE) : DAMAGE;
    }

    // 动态更新真实的 baseDamage
    private void updateBaseDamage() {
        // 确保在战斗中才进行回合计算（防止在牌库查看时报错）
        if (AbstractDungeon.actionManager != null) {
            // 游戏底层逻辑：第一回合 turn 为 1。经过的回合数 = turn - 1
            int turnsPassed = Math.max(0, AbstractDungeon.actionManager.turn - 1);
            // 动态重写基础伤害：初始伤害 + (经过回合数 * 额外伤害)
            this.baseDamage = getOriginalBaseDamage() + (turnsPassed * this.magicNumber);
        }
    }

    @Override
    public void applyPowers() {
        this.updateBaseDamage();
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        this.updateBaseDamage();
        super.calculateCardDamage(mo);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(
                m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL
        ));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}