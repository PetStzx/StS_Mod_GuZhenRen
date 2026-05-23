package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;

public class HuaShaPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("HuaShaPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public HuaShaPower(AbstractCreature owner) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/HuaShaPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/HuaShaPower.png");
        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0];
    }

    public void triggerHuaSha(int lostBlock) {
        this.flash();
        AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD));

        this.addToBot(new DamageAllEnemiesAction(
                this.owner,
                DamageInfo.createDamageMatrix(lostBlock, true),
                DamageInfo.DamageType.THORNS,
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL
        ));
    }
}