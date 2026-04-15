package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.JianQiaoGuAction;
import GuZhenRen.patches.CardColorEnum;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JianQiaoGu extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("JianQiaoGu");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JianQiaoGu.png");

    private static final int COST = 1;
    private static final int BLOCK = 6;
    private static final int UPGRADE_PLUS_BLOCK = 3;
    private static final int INITIAL_RANK = 4;

    public JianQiaoGu() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);

        this.setDao(Dao.JIAN_DAO);
        this.setRank(INITIAL_RANK);

        this.baseBlock = this.block = BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, p, this.block));
        this.addToBot(new JianQiaoGuAction());
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_PLUS_BLOCK);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    @AbstractCardModifier.SaveIgnore
    public static class JianQiaoModifier extends AbstractCardModifier {
        public static final String MODIFIER_ID = GuZhenRen.makeID("JianQiaoModifier");

        @Override
        public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
            if (damage > 0) {
                return damage * 2;
            }
            return damage;
        }

        @Override
        public void onInitialApplication(AbstractCard card) {
            card.freeToPlayOnce = true;
            card.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }

        @Override
        public void onUpdate(AbstractCard card) {
            if (!card.freeToPlayOnce) {
                card.freeToPlayOnce = true;
            }
        }

        @Override
        public boolean removeOnCardPlayed(AbstractCard card) {
            return true;
        }

        @Override
        public void onRemove(AbstractCard card) {
            card.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }

        @Override
        public String identifier(AbstractCard card) {
            return MODIFIER_ID;
        }

        @Override
        public AbstractCardModifier makeCopy() {
            return new JianQiaoModifier();
        }
    }
}