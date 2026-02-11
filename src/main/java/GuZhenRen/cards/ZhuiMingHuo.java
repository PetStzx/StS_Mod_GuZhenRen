package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.powers.ZhuiMingHuoPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhuiMingHuo extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("ZhuiMingHuo");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhuiMingHuo.png");

    private static final int COST = 1;
    private static final int POWER_AMT = 1;

    public ZhuiMingHuo() {

        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.ENEMY);

        // 设置流派 (父类会自动将其名字加到描述开头)
        this.setDao(Dao.YAN_DAO);

        // 设置本卡特有的属性：消耗
        this.exhaust = true;

        this.baseMagicNumber = this.magicNumber = POWER_AMT;

        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(m, p,
                new ZhuiMingHuoPower(m, this.magicNumber), this.magicNumber));
    }

}