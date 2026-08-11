package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.vfx.combat.OfferingEffect;

public class XueRanZhengPao extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("XueRanZhengPao");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueRanZhengPao.png");

    private static final int COST = 0;

    private boolean showDynamicText = false;

    public XueRanZhengPao() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.SELF);

        this.setDao(Dao.XUE_DAO);

        this.baseBlock = this.block = 0;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new VFXAction(new OfferingEffect(), 0.1F));
        this.addToBot(new LoseHPAction(p, p, 1));
        this.addToBot(new GainBlockAction(p, p, this.block));
    }

    private int calculateBlockAmount() {
        if (!AbstractDungeon.isPlayerInDungeon() || AbstractDungeon.player == null) return 0;
        int missingHp = AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth;
        if (missingHp < 0) missingHp = 0;

        return missingHp + 1;
    }

    @Override
    protected String constructRawDescription() {
        String s = super.constructRawDescription();
        if (this.showDynamicText) {
            s += cardStrings.EXTENDED_DESCRIPTION[0];
        }
        return s;
    }

    @Override
    public void applyPowers() {
        this.baseBlock = calculateBlockAmount();
        super.applyPowers();

        this.showDynamicText = true;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        this.baseBlock = calculateBlockAmount();
        super.calculateCardDamage(mo);

        this.showDynamicText = true;
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
}