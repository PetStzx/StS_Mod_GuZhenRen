package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.GuZhenRenTags;
import GuZhenRen.relics.AbstractKongQiao;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;

public class XianQiaoBengKuiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XianQiaoBengKuiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public XianQiaoBengKuiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/XianQiaoBengKuiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XianQiaoBengKuiPower.png");

        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void onInitialApplication() {
        AbstractDungeon.player.gameHandSize -= 2;
    }

    @Override
    public void onRemove() {
        AbstractDungeon.player.gameHandSize += 2;
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        this.addToBot(new LoseEnergyAction(2));
        this.addToBot(new LoseHPAction(this.owner, this.owner, 10));

        if (this.amount > 0) {
            this.amount--;
            if (this.amount == 0) {
                triggerDestruction();
                this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            }
            this.updateDescription();
        }
    }

    private void triggerDestruction() {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 1. 大师牌库查杀
                ArrayList<AbstractCard> masterToRemove = new ArrayList<>();
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c.hasTag(GuZhenRenTags.BEN_MING_GU)) {
                        masterToRemove.add(c);
                    }
                }
                for (AbstractCard c : masterToRemove) {
                    AbstractDungeon.player.masterDeck.removeCard(c);
                }

                // 2. 战斗牌库查杀
                CardGroup[] combatGroups = new CardGroup[] {
                        AbstractDungeon.player.hand,
                        AbstractDungeon.player.drawPile,
                        AbstractDungeon.player.discardPile,
                        AbstractDungeon.player.exhaustPile,
                        AbstractDungeon.player.limbo
                };

                for (CardGroup group : combatGroups) {
                    ArrayList<AbstractCard> toRemove = new ArrayList<>();
                    for (AbstractCard c : group.group) {
                        if (c.hasTag(GuZhenRenTags.BEN_MING_GU)) {
                            toRemove.add(c);
                        }
                    }
                    for (AbstractCard c : toRemove) {
                        AbstractDungeon.topLevelEffectsQueue.add(new PurgeCardEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                        group.removeCard(c);
                    }
                }
                AbstractDungeon.player.hand.refreshHandLayout();

                // 3. 摧毁9转及以下的空窍遗物
                ArrayList<String> relicsToRemove = new ArrayList<>();
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r instanceof AbstractKongQiao) {
                        if (((AbstractKongQiao)r).rank < 10) {
                            relicsToRemove.add(r.relicId);
                        }
                    }
                }
                for (String rId : relicsToRemove) {
                    AbstractDungeon.player.loseRelic(rId);
                }

                this.isDone = true;
            }
        });
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}