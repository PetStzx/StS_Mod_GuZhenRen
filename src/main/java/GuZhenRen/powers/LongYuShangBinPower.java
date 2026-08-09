package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.monsters.LongGong;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class LongYuShangBinPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("LongYuShangBinPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public LongYuShangBinPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/LongYuShangBinPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/LongYuShangBinPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);

        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        int strGain = this.amount / 10;
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + strGain + DESCRIPTIONS[2];
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) {
            // 龙公复生回合不触发
            if (this.owner instanceof LongGong) {
                LongGong lg = (LongGong) this.owner;
                if (lg.nextMove == LongGong.INTENT_SAN_QI_GUI_LAI) {
                    return;
                }
            }

            this.flash();
            final int powerAmount = this.amount;

            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                @Override
                public void update() {
                    if (owner.maxHealth <= powerAmount) {
                        AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
                            @Override
                            public void update() {
                                if (!owner.isDying && !owner.isDead) {
                                    owner.maxHealth = 1;
                                    if (owner.currentHealth > 1) {
                                        owner.currentHealth = 1;
                                    }
                                    owner.healthBarUpdatedEvent();
                                }
                                this.isDone = true;
                            }
                        });
                        AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.common.InstantKillAction(owner));
                    } else {
                        owner.maxHealth -= powerAmount;

                        if (owner.currentHealth > owner.maxHealth) {
                            int hpLoss = owner.currentHealth - owner.maxHealth;
                            owner.currentHealth = Math.max(owner.maxHealth, 1);

                            DamageInfo fakeInfo = new DamageInfo(owner, hpLoss, DamageInfo.DamageType.HP_LOSS);
                            for (AbstractPower p : owner.powers) {
                                p.wasHPLost(fakeInfo, hpLoss);
                            }
                        }
                        owner.healthBarUpdatedEvent();
                    }
                    this.isDone = true;
                }
            });

            int strGain = this.amount / 10;
            if (strGain > 0) {
                AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, strGain), strGain)
                );
            }
        }
    }
}