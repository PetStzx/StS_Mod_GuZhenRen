package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.WanXingFeiYingPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WanXingFeiYing extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("WanXingFeiYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WanXingFeiYing.png");

    private static final int COST = 1;
    private static final int DURATION = 4;
    private static final int NIAN_BASE = 1;

    public WanXingFeiYing() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);

        // MagicNumber: 持续回合数
        this.baseMagicNumber = this.magicNumber = DURATION;

        // SecondMagicNumber: 获得的念数量 (固定为1，不吃加成)
        this.baseSecondMagicNumber = this.secondMagicNumber = NIAN_BASE;

        this.initializeDescription();
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 直接传入基础的 magicNumber 和 secondMagicNumber
        this.addToBot(new ApplyPowerAction(p, p,
                new WanXingFeiYingPower(p, this.magicNumber, this.secondMagicNumber),
                this.magicNumber));
    }
}