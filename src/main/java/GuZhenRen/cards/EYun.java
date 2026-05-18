package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.util.IProbabilityCard;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

public class EYun extends CustomCard {
    public static final String ID = GuZhenRen.makeID("EYun");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/EYun.png");

    private static final int COST = -2;
    private static final int COMBATS_TO_REMOVE = 3;

    public EYun() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.CURSE,
                CardColor.CURSE,
                CardRarity.CURSE,
                CardTarget.NONE);

        this.misc = COMBATS_TO_REMOVE;
        this.baseMagicNumber = this.magicNumber = this.misc;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public void triggerWhenDrawn() {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (AbstractDungeon.player != null) {

                    ArrayList<AbstractCard> allCombatCards = new ArrayList<>();
                    allCombatCards.addAll(AbstractDungeon.player.hand.group);
                    allCombatCards.addAll(AbstractDungeon.player.drawPile.group);
                    allCombatCards.addAll(AbstractDungeon.player.discardPile.group);
                    allCombatCards.addAll(AbstractDungeon.player.exhaustPile.group);
                    allCombatCards.addAll(AbstractDungeon.player.limbo.group);

                    for (AbstractCard c : allCombatCards) {
                        if (c instanceof IProbabilityCard) {
                            // 降低 10% 的基础概率
                            ((IProbabilityCard) c).increaseBaseChance(-0.10f);

                            if (AbstractDungeon.player.hand.contains(c)) {
                                c.superFlash(Color.PURPLE.cpy());
                            }

                        }
                    }
                }
                this.isDone = true;
            }
        });
    }

    public void updateMiscAndMagic() {
        this.baseMagicNumber = this.magicNumber = this.misc;
        this.initializeDescription();
    }

    @Override
    public void applyPowers() {
        this.updateMiscAndMagic();
        super.applyPowers();
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard c = super.makeStatEquivalentCopy();
        c.misc = this.misc;
        if (c instanceof EYun) {
            ((EYun) c).updateMiscAndMagic();
        }
        return c;
    }

    @Override
    public AbstractCard makeCopy() {
        return new EYun();
    }

    @Override
    public void upgrade() {
    }

    @SpirePatch(clz = AbstractRoom.class, method = "endBattle")
    public static class EYunCountdownPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractRoom __instance) {
            if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) return;

            ArrayList<AbstractCard> toRemove = new ArrayList<>();

            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c instanceof EYun) {
                    c.misc--;

                    if (c.misc <= 0) {
                        toRemove.add(c);
                    } else {
                        ((EYun) c).updateMiscAndMagic();
                    }
                }
            }

            for (AbstractCard c : toRemove) {
                AbstractDungeon.player.masterDeck.removeCard(c);
            }
        }
    }
}