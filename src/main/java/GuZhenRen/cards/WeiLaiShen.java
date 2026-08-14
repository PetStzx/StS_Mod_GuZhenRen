package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.relics.WeiLaiShenRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.ApotheosisAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WeiLaiShen extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("WeiLaiShen");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WeiLaiShen.png");

    private static final int COST = 3;
    private static final int SUSTAIN_DURATION = 3;

    public WeiLaiShen() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.NONE);

        this.setDao(Dao.ZHOU_DAO);

        this.baseMagicNumber = this.magicNumber = SUSTAIN_DURATION;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.purgeOnUse = true;

        this.addToBot(new ApotheosisAction());

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {

                if (p.hasRelic(WeiLaiShenRelic.ID)) {
                    WeiLaiShenRelic existingRelic = (WeiLaiShenRelic) p.getRelic(WeiLaiShenRelic.ID);
                    existingRelic.setCounter(WeiLaiShen.this.magicNumber);
                    existingRelic.flash();
                } else {
                    WeiLaiShenRelic newRelic = new WeiLaiShenRelic();
                    newRelic.setCounter(WeiLaiShen.this.magicNumber);

                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                            Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, newRelic
                    );

                    p.masterDeck.group.stream()
                            .filter(c -> c.cardID.equals(WeiLaiShen.ID))
                            .findFirst()
                            .ifPresent(c -> p.masterDeck.removeCard(c));
                }

                this.isDone = true;
            }
        });
    }
}