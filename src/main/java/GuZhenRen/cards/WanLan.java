package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class WanLan extends AbstractGuZhenRenCard {

    public static final String ID = GuZhenRen.makeID("WanLan");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WanLan.png");

    private static final int COST = 1;
    private static final int BASE_DAMAGE = 2;
    private static final int MAGIC = 1;       // 基础恢复 1 点能量
    private static final int UPGRADE_PLUS_MAGIC = 1; // 升级额外恢复 1 点能量 (共 2 点)
    private static final int HITS = 4;        // 攻击 4 次
    private static final int INITIAL_RANK = 6;

    public WanLan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);

        this.setDao(Dao.LI_DAO);
        this.setRank(INITIAL_RANK);

        this.baseDamage = this.damage = BASE_DAMAGE;
        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < HITS; i++) {
            this.addToBot(new DamageAction(
                    m,
                    new DamageInfo(p, this.damage, this.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT
            ));
        }

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (EnergyPanel.totalCount == 0) {
                    AbstractDungeon.actionManager.addToTop(new GainEnergyAction(magicNumber));
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1);
            this.myBaseDescription = UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }
}