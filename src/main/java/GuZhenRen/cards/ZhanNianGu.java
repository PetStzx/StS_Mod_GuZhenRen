package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhanNianGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ZhanNianGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhanNianGu.png");

    private static final int COST = 1;

    // 伤害数值
    private static final int DAMAGE = 9;
    private static final int UPGRADE_PLUS_DAMAGE = 2; // 升级后变 11

    // 念层数数值
    private static final int NIAN_AMT = 4;
    private static final int UPGRADE_PLUS_NIAN = 1; // 升级后变 5

    private static final int INITIAL_RANK = 2; // 2转

    public ZhanNianGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.ZHI_DAO);

        this.baseDamage = this.damage = DAMAGE;
        this.baseNian = this.nian = NIAN_AMT;

        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

        // 2. 如果打出的上一张是攻击牌，获得念
        if (isLastCardAttack()) {
            this.addToBot(new ApplyPowerAction(p, p, new NianPower(p, this.nian), this.nian));
        }
    }

    // 判断上一张打出的牌是否为攻击牌
    private boolean isLastCardAttack() {
        if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() >= 2) {
            AbstractCard lastCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 2);
            return lastCard.type == CardType.ATTACK;
        }
        return false;
    }

    // 满足条件时卡牌边框发金光提示
    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();

        if (!AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty()) {
            AbstractCard lastCard = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1);
            if (lastCard.type == CardType.ATTACK) {
                this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.upgradeNian(UPGRADE_PLUS_NIAN);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}