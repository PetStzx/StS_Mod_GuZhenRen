package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.util.ArrayList;

public class TongXinPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("TongXinPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public TongXinPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/TongXinPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/TongXinPower.png");
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        String coloredName = this.owner.name.replace(" ", " #y");
        this.description = DESCRIPTIONS[0] + coloredName + DESCRIPTIONS[1];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!card.isInAutoplay && !card.purgeOnUse) {
            this.flash();
            this.addToBot(new TongXinAction());
        }
    }

    private static class TongXinAction extends AbstractGameAction {
        public TongXinAction() {
            this.actionType = ActionType.WAIT;
        }

        @Override
        public void update() {
            boolean canPlayDueToNormality = true;
            for (AbstractCard hc : AbstractDungeon.player.hand.group) {
                if (hc.cardID.equals("Normality") && AbstractDungeon.actionManager.cardsPlayedThisTurn.size() >= 3) {
                    canPlayDueToNormality = false;
                    break;
                }
            }

            ArrayList<AbstractCard> candidates = new ArrayList<>();

            if (canPlayDueToNormality) {
                for (AbstractCard c : AbstractDungeon.player.hand.group) {
                    if (c.cost == -2) continue;

                    boolean hasEnergy = c.freeToPlayOnce || c.costForTurn <= EnergyPanel.totalCount || c.cost == -1;

                    if (hasEnergy) {
                        candidates.add(c);
                    }
                }
            }

            if (!candidates.isEmpty()) {
                AbstractCard c = candidates.get(AbstractDungeon.cardRandomRng.random(candidates.size() - 1));
                AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);

                if (!c.freeToPlayOnce) {
                    if (c.costForTurn > 0) {
                        AbstractDungeon.player.energy.use(c.costForTurn);
                    } else if (c.cost == -1) {
                        c.energyOnUse = EnergyPanel.totalCount;
                        AbstractDungeon.player.energy.use(EnergyPanel.totalCount);
                    }
                }
                c.isInAutoplay = true;
                AbstractDungeon.actionManager.addToTop(new NewQueueCardAction(c, target, false, true));
            }

            this.isDone = true;
        }
    }
}