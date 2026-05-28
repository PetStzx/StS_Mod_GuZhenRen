package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class LuoXuanGuQiangGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LuoXuanGuQiangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LuoXuanGuQiangGu.png");

    private static final int COST = 1;
    private static final int DAMAGE = 9;
    private static final int UPGRADE_PLUS_DMG = 3;

    private static final int GOLD_AMT = 90;
    private static final int UPGRADE_PLUS_GOLD = 30;

    private static final int INITIAL_RANK = 2;

    public LuoXuanGuQiangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.ENEMY);

        this.setDao(Dao.GU_DAO);

        this.baseDamage = this.damage = DAMAGE;
        this.baseMagicNumber = this.magicNumber = GOLD_AMT;

        this.setRank(INITIAL_RANK);

        DamageModifierManager.addModifier(this, new IgnoreBlockModifier());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DMG);
            this.upgradeMagicNumber(UPGRADE_PLUS_GOLD);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    public static class IgnoreBlockModifier extends AbstractDamageModifier {
        @Override
        public boolean ignoresBlock(AbstractCreature target) {
            return true;
        }

        @Override
        public AbstractDamageModifier makeCopy() {
            return new IgnoreBlockModifier();
        }
    }
}