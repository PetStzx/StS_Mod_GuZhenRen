package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.JianHenPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JianYingGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("JianYingGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JianYingGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 4;
    private static final int MAGIC = 2; // 基础给予 2 层剑痕
    private static final int UPGRADE_PLUS_MAGIC = 3; // 升级增加 3 层剑痕
    private static final int INITIAL_RANK = 2; // 2转起步

    public JianYingGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON, // 白卡
                CardTarget.ENEMY);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);
        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;

        this.cardsToPreview = new JianYing();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

        // 2. 给予剑痕
        this.addToBot(new ApplyPowerAction(m, p, new JianHenPower(m, this.magicNumber), this.magicNumber));

        // 3. 将 1 张《剑影》加入手牌
        // 第二个参数 true 表示牌加入手牌时会有发光特效
        this.addToBot(new MakeTempCardInHandAction(new JianYing(), 1, true));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC); // 剑痕 3 -> 5
            this.upgradeRank(1); // 2转 -> 3转

            // 如果衍生牌《剑影》也有升级版本，可以在这里让预览的牌也升级
            // this.cardsToPreview.upgrade();

            this.initializeDescription();
        }
    }
}