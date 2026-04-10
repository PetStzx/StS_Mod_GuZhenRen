package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.*;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YuPiGu extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("YuPiGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YuPiGu.png");

    private static final int COST = 1;
    private static final int BLOCK_AMT = 5;
    private static final int UPGRADE_PLUS_BLOCK = 3; // 升级增加3点格挡
    private static final int INITIAL_RANK = 1;

    public YuPiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.BASIC,
                CardTarget.SELF);

        this.baseBlock = BLOCK_AMT;

        this.setDao(Dao.JIN_DAO);


        this.setRank(INITIAL_RANK);
        this.tags.add(CardTags.STARTER_DEFEND);
        this.setRank(INITIAL_RANK);

        // 添加基础防御标签
        this.tags.add(CardTags.STARTER_DEFEND);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_PLUS_BLOCK);
            this.setRank(2);

            this.initializeDescription();
        }
    }
}