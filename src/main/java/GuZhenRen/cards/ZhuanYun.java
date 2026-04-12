package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.powers.ZhuanYunPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhuanYun extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ZhuanYun");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhuanYun.png");

    private static final int COST = 1;
    private static final int MAGIC = 4;
    private static final int UPGRADE_PLUS_MAGIC = 2; // 升级后增加 2，变为 6
    private static final int INITIAL_RANK = 6;

    public ZhuanYun() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.YUN_DAO);
        this.setRank(INITIAL_RANK);
        this.baseMagicNumber = this.magicNumber = MAGIC;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new ZhuanYunPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}