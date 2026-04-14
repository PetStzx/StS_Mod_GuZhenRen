package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class XueYuanMarkPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("XueYuanMarkPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public XueYuanMarkPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/XueYuanPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/XueYuanPower.png");
        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        String playerName = AbstractDungeon.player != null ? AbstractDungeon.player.title : "";
        this.description = playerName + DESCRIPTIONS[0] + this.owner.name + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && AbstractDungeon.player.hasPower(XueDianXingBanPower.POWER_ID)) {
            return damage * 0.5F;
        }
        return damage;
    }

    @Override
    public void onRemove() {
        checkAndRemovePlayerPower();
    }

    @Override
    public void onDeath() {
        checkAndRemovePlayerPower();
    }

    private void checkAndRemovePlayerPower() {
        boolean hasOther = false;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo != this.owner && !mo.isDeadOrEscaped() && mo.hasPower(XueYuanMarkPower.POWER_ID)) {
                hasOther = true;
                break;
            }
        }

        if (!hasOther && AbstractDungeon.player.hasPower(XueYuanPower.POWER_ID)) {
            this.addToTop(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, XueYuanPower.POWER_ID));
        }
    }
}