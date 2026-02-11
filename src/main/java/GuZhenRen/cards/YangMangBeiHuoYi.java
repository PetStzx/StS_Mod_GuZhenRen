package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.YangMangBeiHuoYiPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YangMangBeiHuoYi extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("YangMangBeiHuoYi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YangMangBeiHuoYi.png");

    private static final int COST = 2;
    private static final int BLOCK_AMT = 24;
    private static final int TIMES = 3;
    private static final int BURN_AMT = 1;

    public YangMangBeiHuoYi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.SELF);

        this.setDao(Dao.YAN_DAO);

        this.baseBlock = this.block = BLOCK_AMT;

        // 第一魔法值：反击次数
        this.baseMagicNumber = this.magicNumber = TIMES;

        // 第二魔法值：每次给予的焚烧层数
        this.baseSecondMagicNumber = this.secondMagicNumber = BURN_AMT;

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得格挡
        this.addToBot(new GainBlockAction(p, p, this.block));

        // 2. 给予状态
        this.addToBot(new ApplyPowerAction(p, p,
                new YangMangBeiHuoYiPower(p, this.magicNumber, this.secondMagicNumber),
                this.magicNumber));
    }
}