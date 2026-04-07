package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.HaoYouPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction; // 【修改点1】：引入弃牌堆动作
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SongYouFeng extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("SongYouFeng");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/SongYouFeng.png");

    private static final int COST = 1;

    public SongYouFeng() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ENEMY);

        this.setDao(Dao.FENG_DAO);

        // 预览牌
        this.cardsToPreview = new SongYouFengSongBie();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 结交好友
        this.addToBot(new ApplyPowerAction(m, p, new HaoYouPower(m)));

        // 2. 将“送别”放入弃牌堆
        this.addToBot(new MakeTempCardInDiscardAction(this.cardsToPreview.makeStatEquivalentCopy(), 1));
    }
}