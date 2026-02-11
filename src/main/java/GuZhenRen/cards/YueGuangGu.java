package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YueGuangGu extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("YueGuangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YueGuangGu.png");

    private static final int COST = 1;
    private static final int ATTACK_DMG = 6;
    private static final int UPGRADE_PLUS_DMG = 3;

    private static final int INITIAL_RANK = 1; // 初始一转

    public YueGuangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.BASIC,
                CardTarget.ENEMY);

        this.damage = this.baseDamage = ATTACK_DMG;

        this.setDao(Dao.GUANG_DAO);

        this.setRank(INITIAL_RANK);

        this.tags.add(CardTags.STRIKE);
        this.tags.add(AbstractPlayerEnum.FANG_YUAN.ordinal() > 0 ? CardTags.STARTER_STRIKE : CardTags.STRIKE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DMG);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}