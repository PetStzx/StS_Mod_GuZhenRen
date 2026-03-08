package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.JianYingAction;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.powers.JianHenPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JianYing extends CustomCard {
    public static final String ID = GuZhenRen.makeID("JianYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JianYing.png");

    private static final int COST = 0;

    // 用于打出时给予的剑痕层数
    private static final int MAGIC = 3;
    private static final int UPGRADE_PLUS_MAGIC = 1; // 升级后变 4

    // 用于被消耗时造成的伤害
    private static final int DAMAGE = 4;
    private static final int UPGRADE_PLUS_DMG = 1; // 升级后变 5

    public JianYing() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.ENEMY);

        this.tags.add(GuZhenRenTags.JIAN_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.baseDamage = this.damage = DAMAGE;

        this.isEthereal = true; // 虚无
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(m, p, new JianHenPower(m, this.magicNumber), this.magicNumber));
    }


    @Override
    public void triggerOnExhaust() {
        this.addToBot(new JianYingAction(this));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeDamage(UPGRADE_PLUS_DMG);
            this.initializeDescription();
        }
    }
}