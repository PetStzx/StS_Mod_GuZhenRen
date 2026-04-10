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

    // 次数：基础 2 次，升级加 1 次
    private static final int TIMES = 2;
    private static final int UPGRADE_PLUS_TIMES = 1;

    // 焚烧：基础 3 层，升级减 1 层
    private static final int FEN_SHAO_BASE = 3;
    private static final int UPGRADE_PLUS_FEN_SHAO = -1;

    public LiaoYuanHuo() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.YAN_DAO);

        this.baseMagicNumber = this.magicNumber = TIMES;
        this.baseFenShao = this.fenShao = FEN_SHAO_BASE;

        this.setRank(INITIAL_RANK);
        this.cardsToPreview = new Burn();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 特效：全屏火焰
        this.addToBot(new VFXAction(p, new ScreenOnFireEffect(), 1.0F));

        // 全体焚烧施加
        for (int i = 0; i < this.magicNumber; i++) {
            if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (!mo.isDead && !mo.isDying) {
                        this.addToBot(new ApplyPowerAction(mo, p,
                                new FenShaoPower(mo, this.fenShao),
                                this.fenShao, true));
                    }
                }
            }
        }

        this.addToBot(new MakeTempCardInHandAction(new Burn(), 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1);

            this.upgradeMagicNumber(UPGRADE_PLUS_TIMES);
            this.upgradeFenShao(UPGRADE_PLUS_FEN_SHAO);

            this.initializeDescription();
        }
    }
}