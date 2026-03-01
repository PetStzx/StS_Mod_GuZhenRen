package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.JianQiGuAction;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JianQiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("JianQiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JianQiGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int MAGIC = 2;          // 每层剑痕造成的群体伤害
    private static final int UPGRADE_MAGIC = 1;  // 升级后变为 3
    private static final int INITIAL_RANK = 4;   // 4转起步

    public JianQiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON, // 白卡
                CardTarget.ENEMY);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);

        this.baseDamage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 直接将目标、伤害信息和乘数交给专属动作处理
        this.addToBot(new JianQiGuAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_MAGIC); // AOE 伤害 2 -> 3
            this.upgradeRank(1);                    // 转数 4转 -> 5转
            this.initializeDescription();
        }
    }
}