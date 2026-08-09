package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.IProbabilityCard;
import GuZhenRen.util.ProbabilityHelper;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class YiLuan extends CustomCard implements IProbabilityCard {
    public static final String ID = GuZhenRen.makeID("YiLuan");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/YiLuan.png");

    private static final int COST = -2;
    private static final int HP_LOSS = 6;

    // “抵抗干扰”的概率
    public float baseChance = 0.50f;

    public YiLuan() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.STATUS,
                CardColor.COLORLESS,
                CardRarity.SPECIAL,
                CardTarget.NONE);

        this.baseMagicNumber = this.magicNumber = HP_LOSS;
        this.isEthereal = true;

        this.updateDynamicDescription();
    }

    private void updateDynamicDescription() {
        float modifiedChance = ProbabilityHelper.getModifiedChance(this, this.baseChance);

        int failPct = Math.round((1.0f - modifiedChance) * 100);
        int baseFailPct = Math.round((1.0f - this.baseChance) * 100);

        String color = "";
        if (failPct < baseFailPct) {
            color = "[#7fff00]";
        } else if (failPct > baseFailPct) {
            color = "[#ff6563]";
        }

        String chanceStr = color.isEmpty() ? (failPct + "%") : (color + failPct + "%[]");

        this.rawDescription = DESCRIPTION.replace("{CHANCE}", chanceStr);
        this.initializeDescription();
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        this.updateDynamicDescription();
    }

    @Override
    public void increaseBaseChance(float amount) {
        this.baseChance += amount;
        if (this.baseChance > 1.0f) this.baseChance = 1.0f;
        if (this.baseChance < 0.0f) this.baseChance = 0.0f;
        this.updateDynamicDescription();
    }

    @Override
    public float getBaseChance() {
        return this.baseChance;
    }

    @Override
    public AbstractCard makeCopy() {
        YiLuan c = new YiLuan();
        c.baseChance = this.baseChance;
        return c;
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void upgrade() {
    }

    public boolean tryTriggerFailure() {
        if (!ProbabilityHelper.rollProbability(this, this.baseChance)) {
            this.superFlash(Color.PURPLE.cpy());
            CardCrawlGame.sound.play("NULLIFY_SFX");
            AbstractDungeon.actionManager.addToBottom(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, this.magicNumber));
            return true;
        }
        return false;
    }
}