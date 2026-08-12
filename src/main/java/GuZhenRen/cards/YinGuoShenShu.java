package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.YinPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YinGuoShenShu extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("YinGuoShenShu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YinGuoShenShu.png");

    private static final int COST = 3;

    public YinGuoShenShu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardTarget.SELF);

        this.setDao(Dao.MU_DAO); // 木道

        // 绑定卡牌预览，鼠标悬停时会显示“来因去果”
        this.cardsToPreview = new LaiYinQuGuo();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得“因”Power
        this.addToBot(new ApplyPowerAction(p, p, new YinPower(p)));

        // 2. 将一张“来因去果”加入弃牌堆
        this.addToBot(new MakeTempCardInDiscardAction(this.cardsToPreview.makeStatEquivalentCopy(), 1));
    }
}