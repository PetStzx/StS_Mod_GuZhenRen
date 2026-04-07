package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class FeiXiongZhiLiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("FeiXiongZhiLiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/FeiXiongZhiLiGu.png");

    private static final int COST = 2;
    private static final int DAMAGE = 12;
    private static final int VULN = 2;
    private static final int UPGRADE_PLUS_VULN = 2; // 升级+2，总共4层易伤
    private static final int INITIAL_RANK = 6; // 金卡（仙蛊）起步6转

    public FeiXiongZhiLiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE, // 金卡
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);

        this.baseDamage = DAMAGE;
        this.isMultiDamage = true; // 开启群体伤害

        this.baseMagicNumber = this.magicNumber = VULN;
        this.exhaust = true;

        this.cardsToPreview = new FeiXiongXuYing();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成群体伤害
        this.addToBot(new DamageAllEnemiesAction(
                p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.BLUNT_HEAVY
        ));

        // 2. 给予群体易伤
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                this.addToBot(new ApplyPowerAction(
                        mo, p, new VulnerablePower(mo, this.magicNumber, false), this.magicNumber, true
                ));
            }
        }

        // 3. 生成飞熊虚影
        AbstractCard c = this.cardsToPreview.makeStatEquivalentCopy();
        this.addToBot(new MakeTempCardInHandAction(c, 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_VULN); // 2 -> 4层易伤
            this.upgradeRank(1); // 6转 -> 7转
            this.cardsToPreview.upgrade();
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}