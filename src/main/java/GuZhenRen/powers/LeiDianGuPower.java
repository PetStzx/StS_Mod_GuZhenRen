package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;

public class LeiDianGuPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("LeiDianGuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public LeiDianGuPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;

        this.type = PowerType.BUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/LeiDianGuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/LeiDianGuPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void atStartOfTurn() {
        if (this.amount == 1) {
            this.flash();

            for (int i = 0; i < 5; i++) {
                DamageInfo info = new DamageInfo(this.owner, 8, DamageInfo.DamageType.THORNS);
                this.addToBot(new LeiDianGuDamageAction(AbstractDungeon.player, info));
            }

            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    LeiDianGuPower.this.amount = 3;
                    LeiDianGuPower.this.updateDescription();
                    this.isDone = true;
                }
            });
        } else {
            this.flash();
            this.addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
        }
    }

    public static class LeiDianGuDamageAction extends AbstractGameAction {
        private DamageInfo info;

        public LeiDianGuDamageAction(AbstractCreature target, DamageInfo info) {
            this.target = target;
            this.info = info;
            this.actionType = ActionType.DAMAGE;
            this.duration = 0.1F;
        }

        @Override
        public void update() {
            if (this.duration == 0.1F && this.target != null && !this.target.isDeadOrEscaped()) {

                CardCrawlGame.sound.play("ORB_LIGHTNING_EVOKE");
                AbstractDungeon.effectList.add(new LightningEffect(this.target.drawX, this.target.drawY));

                this.target.damage(this.info);

                if (this.target.lastDamageTaken > 0) {
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Dazed(), 1, false, true));
                }
            }
            this.tickDuration();
        }
    }
}