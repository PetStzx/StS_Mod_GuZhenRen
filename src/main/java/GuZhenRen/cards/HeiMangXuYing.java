package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ConstrictedPower;

public class HeiMangXuYing extends AbstractXuYingCard {
    public static final String ID = GuZhenRen.makeID("HeiMangXuYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HeiMangXuYing.png");

    public HeiMangXuYing() {
        super(ID, NAME, IMG_PATH, -2, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardTarget.ENEMY);

        this.baseChanceFloat = 0.25f;
        this.baseMagicNumber = this.magicNumber = 4;
        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(2); // 4层 -> 6层
            this.initializeDescription();
        }
    }

    @Override
    public void triggerPhantomEffect(AbstractMonster m) {
        if (m != null && !m.isDeadOrEscaped()) {
            AbstractPlayer p = AbstractDungeon.player;

            this.addToTop(new SFXAction("MONSTER_ENCOUNTER_SPECIAL"));
            this.addToTop(new ApplyPowerAction(m, p, new ConstrictedPower(m, p, this.magicNumber), this.magicNumber));
        }
    }
}