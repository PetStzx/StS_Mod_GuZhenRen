package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TieGuGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("TieGuGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/TieGuGu.png");

    private static final int COST = 1;
    private static final int BLOCK_AMT = 11;
    private static final int UPGRADE_PLUS_BLOCK = 4; // 升级后增加 4 点，变成 15
    private static final int HP_LOSS = 1; // 失去 2 点生命
    private static final int INITIAL_RANK = 2; // 2转

    public TieGuGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON, // 白卡
                CardTarget.SELF);

        this.setDao(Dao.GU_DAO); // 骨道
        this.setRank(INITIAL_RANK);

        this.baseBlock = this.block = BLOCK_AMT;

        this.baseMagicNumber = this.magicNumber = HP_LOSS;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new LoseHPAction(p, p, this.magicNumber));
        this.addToBot(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_PLUS_BLOCK); // 12 -> 16
            this.upgradeRank(1); // 2转 -> 3转
            this.initializeDescription();
        }
    }
}