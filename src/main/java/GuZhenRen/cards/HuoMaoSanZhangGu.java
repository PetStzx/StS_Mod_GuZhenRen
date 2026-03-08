package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.FenShaoPower;
import GuZhenRen.powers.HuoMaoSanZhangPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuoMaoSanZhangGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuoMaoSanZhangGu");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuoMaoSanZhangGu.png");

    private static final int COST = 2;
    private static final int MAGIC_AMT = 3;
    private static final int INITIAL_RANK = 4;

    public HuoMaoSanZhangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.YAN_DAO);

        // 【极简】直接启用底层焚烧变量
        this.baseFenShao = this.fenShao = MAGIC_AMT;

        this.setRank(INITIAL_RANK);
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 只有升级后才触发全体焚烧
        if (this.upgraded && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.flash();
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (!monster.isDead && !monster.isDying) {
                    // 传入算好道痕加成的 this.fenShao
                    this.addToBot(new ApplyPowerAction(monster, p,
                            new FenShaoPower(monster, this.fenShao), this.fenShao));
                }
            }
        }

        // 2. 给予自身“火冒三丈”能力
        if (!p.hasPower(HuoMaoSanZhangPower.POWER_ID)) {
            this.addToBot(new ApplyPowerAction(p, p, new HuoMaoSanZhangPower(p)));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeRank(1);

            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}