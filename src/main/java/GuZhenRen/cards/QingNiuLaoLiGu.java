package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class QingNiuLaoLiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("QingNiuLaoLiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/QingNiuLaoLiGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 5;
    private static final int UPGRADE_PLUS_DAMAGE = 2;
    private static final int INITIAL_RANK = 2;

    public QingNiuLaoLiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);

        this.baseDamage = DAMAGE;
        this.exhaust = true;

        this.cardsToPreview = new NiuLiXuYing();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        // 2. 将预览的卡牌复制一份加入手牌
        AbstractCard c = this.cardsToPreview.makeStatEquivalentCopy();
        this.addToBot(new MakeTempCardInHandAction(c, 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DAMAGE);
            this.upgradeRank(1); // 2转 -> 3转
            this.cardsToPreview.upgrade();
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}