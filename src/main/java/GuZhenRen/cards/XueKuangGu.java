package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.XueKuangGuAction;
import GuZhenRen.patches.CardColorEnum;
import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class XueKuangGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("XueKuangGu");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/XueKuangGu.png");

    private static final int COST = 1;
    private static final int MAGIC = 2;
    private static final int UPGRADE_PLUS_MAGIC = 1;
    private static final int INITIAL_RANK = 4;

    public XueKuangGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.XUE_DAO);
        this.baseMagicNumber = this.magicNumber = MAGIC;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new XueKuangGuAction(this.magicNumber));
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

    @AbstractCardModifier.SaveIgnore
    public static class XueKuangModifier extends AbstractCardModifier {
        public static final String MODIFIER_ID = GuZhenRen.makeID("XueKuangModifier");

        private boolean inHandLastFrame = false;

        @Override
        public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
            AbstractDungeon.actionManager.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 2));
        }

        @Override
        public void onUpdate(AbstractCard card) {
            if (AbstractDungeon.player == null ||
                    AbstractDungeon.getCurrRoom() == null ||
                    AbstractDungeon.getCurrRoom().phase != com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT) {
                return;
            }

            boolean inHandNow = AbstractDungeon.player.hand.contains(card);

            if (inHandNow && !this.inHandLastFrame) {
                card.freeToPlayOnce = true;
                AbstractDungeon.actionManager.addToBottom(new NewQueueCardAction(card, true, false, true));
            }

            this.inHandLastFrame = inHandNow;
        }

        @Override
        public String modifyDescription(String rawDescription, AbstractCard card) {
            return rawDescription + XueKuangGu.cardStrings.EXTENDED_DESCRIPTION[0];
        }

        @Override
        public void onInitialApplication(AbstractCard card) {
            card.glowColor = Color.SCARLET.cpy();
        }

        @Override
        public String identifier(AbstractCard card) {
            return MODIFIER_ID;
        }

        @Override
        public AbstractCardModifier makeCopy() {
            return new XueKuangModifier();
        }
    }
}