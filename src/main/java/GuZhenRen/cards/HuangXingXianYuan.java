package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuangXingXianYuan extends CustomCard {
    public static final String ID = GuZhenRen.makeID("HuangXingXianYuan");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuangXingXianYuan.png");

    public HuangXingXianYuan() {
        super(ID, NAME, IMG_PATH, 0, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.SPECIAL,
                CardTarget.NONE);

        this.exhaust = true;
        this.selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainEnergyAction(5));

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int toDraw = BaseMod.MAX_HAND_SIZE - p.hand.size();
                if (toDraw > 0) {
                    AbstractDungeon.actionManager.addToTop(new DrawCardAction(p, toDraw));
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
        }
    }
}