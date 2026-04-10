package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.potions.ShengJiYe;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JiuYeShengJiCao extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("JiuYeShengJiCao");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JiuYeShengJiCao.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 2; // 2转

    public JiuYeShengJiCao() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.exhaust = true;
        this.setDao(Dao.MU_DAO);
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获得一份"生机叶"药水
        this.addToBot(new ObtainPotionAction(new ShengJiYe()));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();

            // 升级效果：1费 -> 0费
            this.upgradeBaseCost(0);

            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}