package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MuJiaGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("MuJiaGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/MuJiaGu.png");

    private static final int COST = 1;
    private static final int BLOCK_AMT = 8;
    private static final int UPGRADE_PLUS_BLOCK = 2; // 升级后变 10
    private static final int FEN_SHAO_AMT = 5;
    private static final int UPGRADE_PLUS_FEN_SHAO = 2; // 升级后变 7
    private static final int INITIAL_RANK = 2; // 2转

    public MuJiaGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.COMMON,
                CardTarget.SELF);

        this.setDao(Dao.MU_DAO);
        this.setRank(INITIAL_RANK);

        this.baseBlock = this.block = BLOCK_AMT;

        this.baseFenShao = this.fenShao = FEN_SHAO_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, p, this.block));
    }

    @Override
    public void triggerOnExhaust() {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                if (!mo.isDead && !mo.isDying) {
                    this.addToBot(new ApplyPowerAction(mo, AbstractDungeon.player,
                            new FenShaoPower(mo, this.fenShao), this.fenShao, true));
                }
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_PLUS_BLOCK);
            this.upgradeFenShao(UPGRADE_PLUS_FEN_SHAO);
            this.upgradeRank(1); // 2转 -> 3转
            this.initializeDescription();
        }
    }
}