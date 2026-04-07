package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.YuHuoPatch;
import GuZhenRen.powers.FenShaoPower;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;

public class YuHuo extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("YuHuo");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YuHuo.png");

    private static final int COST = 1;
    private static final int UPGRADE_COST = 0;
    private static final int FEN_SHAO_BASE = 1;
    private static final int INITIAL_RANK = 6;

    private boolean showDynamicText = false;

    public YuHuo() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.ALL_ENEMY);

        this.setDao(Dao.YAN_DAO);
        this.setRank(INITIAL_RANK);

        this.baseFenShao = this.fenShao = FEN_SHAO_BASE;
        this.baseSecondMagicNumber = this.secondMagicNumber = 0;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int count = YuHuoPatch.cardsExhaustedThisTurn;

        if (count > 0) {
            this.addToBot(new VFXAction(p, new ScreenOnFireEffect(), 1.0F));

            for (int i = 0; i < count; i++) {
                if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                    for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                        if (!mo.isDead && !mo.isDying) {
                            this.addToBot(new ApplyPowerAction(mo, p, new FenShaoPower(mo, this.fenShao), this.fenShao, true));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected String constructRawDescription() {
        String s = super.constructRawDescription();
        if (this.showDynamicText && cardStrings.EXTENDED_DESCRIPTION != null) {
            s += cardStrings.EXTENDED_DESCRIPTION[0];
        }
        return s;
    }

    @Override
    public void applyPowers() {
        int amount = YuHuoPatch.cardsExhaustedThisTurn;
        if (this.secondMagicNumber != amount) {
            this.secondMagicNumber = amount;
            this.isSecondMagicNumberModified = true;
        }

        this.showDynamicText = true;
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int amount = YuHuoPatch.cardsExhaustedThisTurn;
        if (this.secondMagicNumber != amount) {
            this.secondMagicNumber = amount;
            this.isSecondMagicNumberModified = true;
        }

        this.showDynamicText = true;
        super.calculateCardDamage(mo);
    }

    @Override
    public void onMoveToDiscard() {
        this.showDynamicText = false;
        this.initializeDescription();
    }

    @Override
    public void triggerOnExhaust() {
        this.showDynamicText = false;
        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(UPGRADE_COST);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }
}