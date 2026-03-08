package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;

public class LiaoYuanHuo extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("LiaoYuanHuo");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/LiaoYuanHuo.png");

    private static final int COST = 1;
    private static final int INITIAL_RANK = 5;

    private static final int TIMES = 3;
    private static final int UPGRADE_TIMES = 1;

    // 基础焚烧层数
    private static final int FEN_SHAO_BASE = 2;

    public LiaoYuanHuo() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.YAN_DAO);

        this.baseMagicNumber = this.magicNumber = TIMES;

        this.baseFenShao = this.fenShao = FEN_SHAO_BASE;

        this.setRank(INITIAL_RANK);

        // 初始预览：普通灼伤
        this.cardsToPreview = new Burn();
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 华丽的屏幕燃烧特效
        this.addToBot(new VFXAction(p, new ScreenOnFireEffect(), 1.0F));

        // 进行 magicNumber 次全体焚烧施加
        for (int i = 0; i < this.magicNumber; i++) {
            if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (!mo.isDead && !mo.isDying) {
                        // 【极简】传入 this.fenShao 即可享受所有加成
                        this.addToBot(new ApplyPowerAction(mo, p,
                                new FenShaoPower(mo, this.fenShao),
                                this.fenShao, true));
                    }
                }
            }
        }

        // 生成灼伤卡
        AbstractCard c = new Burn();
        if (this.upgraded) {
            c.upgrade();
        }
        this.addToBot(new MakeTempCardInHandAction(c, 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_TIMES); // 升级提升次数 3 -> 4
            this.upgradeRank(1);

            // 更新预览卡牌为 灼伤+
            AbstractCard c = new Burn();
            c.upgrade();
            this.cardsToPreview = c;

            this.myBaseDescription = cardStrings.UPGRADE_DESCRIPTION;

            this.initializeDescription();
        }
    }
}