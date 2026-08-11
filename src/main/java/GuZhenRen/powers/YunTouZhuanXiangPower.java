package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;

public class YunTouZhuanXiangPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("YunTouZhuanXiangPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public YunTouZhuanXiangPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;

        if (this.amount > 100) {
            this.amount = 100;
        }

        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/YunTouZhuanXiangPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/YunTouZhuanXiangPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (this.amount > 100) {
            this.amount = 100;
        }
    }

    @Override
    public void atEndOfRound() {
        this.flash();

        if (this.owner instanceof AbstractMonster && !this.owner.isDeadOrEscaped()) {
            AbstractMonster m = (AbstractMonster) this.owner;

            AbstractPower stunPower = m.getPower(StunMonsterPower.POWER_ID);
            boolean willBeStunnedNextTurn = (stunPower != null && stunPower.amount > 1);

            if (!willBeStunnedNextTurn) {
                int roll = AbstractDungeon.miscRng.random(1, 100);
                if (roll <= this.amount) {
                    this.addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            AbstractDungeon.effectList.add(new TextAboveCreatureEffect(m.hb.cX - m.animX, m.hb.cY, DESCRIPTIONS[2], Color.YELLOW.cpy()));
                            this.isDone = true;
                        }
                    });
                    this.addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new StunMonsterPower(m, 1), 1));
                }
            }
        }
        this.addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 20));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}