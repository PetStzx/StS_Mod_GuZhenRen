package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.HuaShiAction;
import GuZhenRen.patches.CardColorEnum;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HuaShiGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("HuaShiGu");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/HuaShiGu.png");

    private static final int COST = 0;
    private static final int MAGIC = 3;
    private static final int UPGRADE_PLUS_MAGIC = 2;
    private static final int INITIAL_RANK = 6;

    public HuaShiGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.NONE);

        this.setDao(Dao.TU_DAO);
        this.setRank(INITIAL_RANK);
        this.baseMagicNumber = this.magicNumber = MAGIC;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new HuaShiAction(this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_PLUS_MAGIC);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }


    @AbstractCardModifier.SaveIgnore
    public static class HuaShiModifier extends AbstractCardModifier {
        public static final String MODIFIER_ID = GuZhenRen.makeID("HuaShiModifier");
        public int amount;

        public HuaShiModifier(int amount) {
            this.amount = amount;
        }

        @Override
        public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
            AbstractDungeon.actionManager.addToBottom(
                    new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, this.amount)
            );
        }

        @Override
        public String modifyDescription(String rawDescription, AbstractCard card) {
            return rawDescription + HuaShiGu.cardStrings.EXTENDED_DESCRIPTION[1] + this.amount + HuaShiGu.cardStrings.EXTENDED_DESCRIPTION[2];
        }

        @Override
        public String identifier(AbstractCard card) {
            return MODIFIER_ID;
        }

        @Override
        public AbstractCardModifier makeCopy() {
            return new HuaShiModifier(this.amount);
        }
    }
}