package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.XuePiaoLiuPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XuePiaoLiu extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("XuePiaoLiu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XuePiaoLiu.png");

    private static final int COST = 1;
    private static final int MAGIC = 1; // 每次掉血抽 1 张牌

    public XuePiaoLiu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardTarget.SELF);

        this.setDao(Dao.XUE_DAO);

        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new XuePiaoLiuPower(p, this.magicNumber), this.magicNumber));
    }
}