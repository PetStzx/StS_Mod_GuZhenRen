package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class JiTuPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("JiTuPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public JiTuPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;

        String pathLarge = GuZhenRen.assetPath("img/powers/JiTuPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/JiTuPower.png");
        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);
        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurn() {
        boolean hasRealBarricade = false;
        boolean hasRealBlur = false;
        for (AbstractPower p : this.owner.powers) {
            if ("Barricade".equals(p.ID)) hasRealBarricade = true;
            if ("Blur".equals(p.ID)) hasRealBlur = true;
        }

        if (!hasRealBarricade && !hasRealBlur) {
            int currentBlock = this.owner.currentBlock;
            int retainLimit = this.amount;

            boolean hasCalipers = this.owner.isPlayer && ((AbstractPlayer)this.owner).hasRelic("Calipers");
            int calipersRetain = hasCalipers ? Math.max(0, currentBlock - 15) : 0;

            // 取 积土 和 外卡钳 中保留效果更好的那个
            int finalRetain = Math.max(retainLimit, calipersRetain);

            if (currentBlock > finalRetain) {
                int amountToLose = currentBlock - finalRetain;
                this.owner.loseBlock(amountToLose, true);
            }
        }
    }
}