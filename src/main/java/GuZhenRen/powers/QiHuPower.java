package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.monsters.LongGong;
import GuZhenRen.monsters.QiQiang;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class QiHuPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("QiHuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public AbstractCreature protectedTarget = null;

    public QiHuPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/QiHuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/QiHuPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        findTarget();
        this.updateDescription();
    }

    public void findTarget() {
        this.protectedTarget = null;

        if (AbstractDungeon.getMonsters() != null) {
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (!m.isDeadOrEscaped() && m.id.equals(LongGong.ID)) {
                    this.protectedTarget = m;
                    break;
                }
            }

            if (this.protectedTarget == null) {
                ArrayList<AbstractMonster> validTargets = new ArrayList<>();
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (!m.isDeadOrEscaped() && !m.id.equals(QiQiang.ID) && m != this.owner) {
                        validTargets.add(m);
                    }
                }
                if (!validTargets.isEmpty()) {
                    this.protectedTarget = validTargets.get(AbstractDungeon.cardRandomRng.random(validTargets.size() - 1));
                }
            }
        }

        if (this.protectedTarget == null) {
            this.protectedTarget = this.owner;
        }
    }

    @Override
    public void updateDescription() {
        if (this.protectedTarget != null) {
            String coloredName = this.protectedTarget.name.replace(" ", " #y");
            this.description = DESCRIPTIONS[0] + "#y" + coloredName + DESCRIPTIONS[1];
        }
    }
}