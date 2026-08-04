package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.CollectorCurseEffect;

public class YingShengChongPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("YingShengChongPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private String targetCardName = "";

    public YingShengChongPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/YingShengChongPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/YingShengChongPower.png");
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.owner.dialogX -= 80.0F * com.megacrit.cardcrawl.core.Settings.scale;
        this.owner.dialogY += 60.0F * com.megacrit.cardcrawl.core.Settings.scale;

        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (targetCardName.isEmpty()) {
            this.description = DESCRIPTIONS[0];
        } else {
            String coloredName = targetCardName.replace(" ", " #y");
            this.description = DESCRIPTIONS[1] + coloredName + DESCRIPTIONS[4];
        }
    }

    private void clearRedGlow() {
        if (!this.targetCardName.isEmpty() && AbstractDungeon.player != null) {

            java.util.ArrayList<AbstractCard> allCombatCards = new java.util.ArrayList<>();
            if (AbstractDungeon.player.hand != null) allCombatCards.addAll(AbstractDungeon.player.hand.group);
            if (AbstractDungeon.player.drawPile != null) allCombatCards.addAll(AbstractDungeon.player.drawPile.group);
            if (AbstractDungeon.player.discardPile != null) allCombatCards.addAll(AbstractDungeon.player.discardPile.group);
            if (AbstractDungeon.player.exhaustPile != null) allCombatCards.addAll(AbstractDungeon.player.exhaustPile.group);
            if (AbstractDungeon.player.limbo != null) allCombatCards.addAll(AbstractDungeon.player.limbo.group);

            for (AbstractCard c : allCombatCards) {
                String cleanName = c.name.replace("+", "").trim();
                if (cleanName.equals(this.targetCardName)) {
                    c.stopGlowing();
                    c.glowColor = new com.badlogic.gdx.graphics.Color(0.2F, 0.9F, 1.0F, 0.25F);
                }
            }

            if (AbstractDungeon.player.hand != null) {
                AbstractDungeon.player.hand.glowCheck();
            }
        }
    }

    public void triggerLockCard() {
        clearRedGlow();

        if (!AbstractDungeon.player.hand.isEmpty()) {
            AbstractCard targetCard = AbstractDungeon.player.hand.getRandomCard(AbstractDungeon.cardRandomRng);
            this.targetCardName = targetCard.name.replace("+", "").trim();

            this.updateDescription();
            this.flash();


            AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                @Override
                public void update() {
                    if (owner != null && !owner.isDeadOrEscaped() && !owner.halfDead && !owner.isDying) {
                        AbstractDungeon.actionManager.addToTop(new TalkAction(owner, targetCardName + DESCRIPTIONS[2], 1.0F, 2.5F));
                    }
                    this.isDone = true;
                }
            });
        } else {
            this.targetCardName = "";
            this.updateDescription();
        }
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if (!this.targetCardName.isEmpty() && AbstractDungeon.player != null && AbstractDungeon.player.hand != null) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                String cleanName = c.name.replace("+", "").trim();
                if (cleanName.equals(this.targetCardName)) {
                    c.glowColor = Color.RED.cpy();
                    if (!c.isGlowing) {
                        c.beginGlowing();
                    }
                }
            }
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!this.targetCardName.isEmpty()) {

            if (this.owner == null || this.owner.isDeadOrEscaped() || this.owner.isDying || this.owner.halfDead) {
                return;
            }

            String playedName = card.name.replace("+", "").trim();

            if (playedName.equals(this.targetCardName)) {
                this.flash();

                AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                    @Override
                    public void update() {
                        card.targetTransparency = 1.0F;
                        card.transparency = 1.0F;
                        this.isDone = true;
                    }
                });

                AbstractDungeon.actionManager.addToTop(new DamageAction(
                        AbstractDungeon.player,
                        new DamageInfo(this.owner, 99999, DamageInfo.DamageType.HP_LOSS)
                ));

                AbstractDungeon.actionManager.addToTop(new WaitAction(1.5F));

                AbstractDungeon.actionManager.addToTop(new VFXAction(
                        new CollectorCurseEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 2.0F));

                AbstractDungeon.actionManager.addToTop(new SFXAction("MONSTER_COLLECTOR_DEBUFF"));

                AbstractDungeon.actionManager.addToTop(new WaitAction(0.5F));

                AbstractDungeon.actionManager.addToTop(new TalkAction(this.owner, DESCRIPTIONS[3], 1.0F, 2.5F));

                AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                    @Override
                    public void update() {
                        card.unhover();
                        card.untip();
                        card.stopGlowing();

                        card.targetTransparency = 0.0F;
                        card.transparency = 0.0F;

                        this.isDone = true;
                    }
                });
            }
        }
    }

    @Override
    public void onRemove() {
        clearRedGlow();
        this.targetCardName = "";
    }

    @Override
    public void onDeath() {
        clearRedGlow();
        this.targetCardName = "";
    }
}