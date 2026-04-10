package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.powers.FenShaoPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuoShi extends AbstractGuZhenRenCard { // 继承蛊虫父类，完美兼容变量
    public static final String ID = GuZhenRen.makeID("HuoShi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuoShi.png");

    private static final int COST = 0;
    private static final int FEN_SHAO_BASE = 2; // 初始给予 2 层
    private static final int UPGRADE_FEN_SHAO = 1; // 升级后 3 层

    public HuoShi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.ENEMY);

        this.tags.add(GuZhenRenTags.YAN_DAO);

        // 使用父类的焚烧属性
        this.baseFenShao = this.fenShao = FEN_SHAO_BASE;
        // 设为0转防止父类某些逻辑报空
        this.setRank(0);
        this.selfRetain = true;
        this.exhaust = true;
    }

    @Override
    protected String constructRawDescription() {
        return DESCRIPTION;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(m, p, new FenShaoPower(m, this.fenShao), this.fenShao));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeFenShao(UPGRADE_FEN_SHAO);
            this.initializeDescription();
        }
    }
}