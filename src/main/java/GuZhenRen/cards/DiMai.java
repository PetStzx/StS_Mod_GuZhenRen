package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.util.BattleStateManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DiMai extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("DiMai");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/DiMai.png");

    private static final int COST = 18;
    private static final int UPGRADE_COST = 15;
    private static final int BLOCK = 40;
    private static final int INITIAL_RANK = 6;

    // 记录本场战斗获得格挡的次数
    public static int blockGainedCountThisCombat = 0;

    static {
        BattleStateManager.onBattleStart(() -> DiMai.blockGainedCountThisCombat = 0);
        BattleStateManager.onPostBattle(() -> DiMai.blockGainedCountThisCombat = 0);
    }

    public DiMai() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);

        this.setDao(Dao.TU_DAO);
        this.baseBlock = this.block = BLOCK;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void applyPowers() {
        super.applyPowers();

        int currentCost = this.cost - DiMai.blockGainedCountThisCombat;
        this.setCostForTurn(currentCost);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(UPGRADE_COST);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}