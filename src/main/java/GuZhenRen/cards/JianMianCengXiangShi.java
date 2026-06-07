package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.relics.JianMianCengXiangShiRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class JianMianCengXiangShi extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("JianMianCengXiangShi");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/JianMianCengXiangShi.png");

    private static final int COST = 2;

    public JianMianCengXiangShi() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.NONE);

        this.setDao(Dao.BIAN_HUA_DAO);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.purgeOnUse = true;

        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (p.hasRelic(JianMianCengXiangShiRelic.ID)) {
                    JianMianCengXiangShiRelic existingRelic = (JianMianCengXiangShiRelic) p.getRelic(JianMianCengXiangShiRelic.ID);
                    existingRelic.setCounter(5);
                    existingRelic.flash();
                } else {
                    // 首次转化为遗物
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                            Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new JianMianCengXiangShiRelic()
                    );

                    p.masterDeck.group.stream()
                            .filter(c -> c.cardID.equals(JianMianCengXiangShi.ID))
                            .findFirst()
                            .ifPresent(c -> p.masterDeck.removeCard(c));
                }

                this.isDone = true;
            }
        });
    }
}