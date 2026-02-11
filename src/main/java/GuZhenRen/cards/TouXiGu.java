package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class TouXiGu extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("TouXiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/TouXiGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 8;
    private static final int UPGRADE_PLUS_DMG = 3;

    private static final int INITIAL_RANK = 2; // 初始2转

    public TouXiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.ENEMY);

        this.baseDamage = DAMAGE;

        // 设置流派：偷道
        this.setDao(Dao.TOU_DAO);


        // 设置转数：2转
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成无视格挡的伤害
        // 技巧：使用 HP_LOSS 类型来实现穿透格挡，但数值使用的是 this.damage (享受力量加成后的数值)
        this.addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, DamageInfo.DamageType.HP_LOSS),
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

        // 2. 给予1层易伤
        this.addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, 1, false), 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DMG);

            // 升级转数：2转 -> 3转
            this.upgradeRank(1);

            this.initializeDescription();
        }
    }
}