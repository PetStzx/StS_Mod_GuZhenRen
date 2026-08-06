package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class JiuLongWenHuShenPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("JiuLongWenHuShenPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public JiuLongWenHuShenPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = Math.min(amount, 9);
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/JiuLongWenHuShenPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/JiuLongWenHuShenPower.png");

        Texture largeTexture = ImageMaster.loadImage(pathLarge);
        Texture smallTexture = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(largeTexture, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(smallTexture, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + (this.amount * 10) + DESCRIPTIONS[1];
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (this.amount > 9) {
            this.amount = 9;
        }
        this.updateDescription();
    }

    // 气墙存在时，不执行减伤
    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            boolean protectedByQiHu = false;

            if (AbstractDungeon.getMonsters() != null) {
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (!mo.isDeadOrEscaped() && mo.hasPower(QiHuPower.POWER_ID)) {
                        QiHuPower qh = (QiHuPower) mo.getPower(QiHuPower.POWER_ID);
                        if (qh.protectedTarget == this.owner) {
                            protectedByQiHu = true;
                            break;
                        }
                    }
                }
            }

            if (!protectedByQiHu) {
                return damage * (1.0F - (this.amount * 0.1F));
            }
        }
        return damage;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != this.owner) {
            this.flash();
            this.addToTop(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
        }
        return damageAmount;
    }
}