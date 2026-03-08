package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractXuYingCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class LiQiPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("LiQiPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public LiQiPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        String pathLarge = GuZhenRen.assetPath("img/powers/LiQiPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/LiQiPower.png");

        Texture texLarge = ImageMaster.loadImage(pathLarge);
        Texture texSmall = ImageMaster.loadImage(pathSmall);

        this.region128 = new TextureAtlas.AtlasRegion(texLarge, 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(texSmall, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atStartOfTurnPostDraw() {
        this.flash();
        this.addToBot(new TriggerRoundAction(this.amount));
    }

    // =========================================================================
    // 触发动作 (内部类)
    // =========================================================================
    private class TriggerRoundAction extends AbstractGameAction {
        private int roundsLeft;

        public TriggerRoundAction(int roundsLeft) {
            this.roundsLeft = roundsLeft;
        }

        @Override
        public void update() {
            if (AbstractDungeon.player != null && this.roundsLeft > 0) {

                // 1. 将【本轮】所有的虚影动画和效果排入底层队列
                for (AbstractCard c : AbstractDungeon.player.hand.group) {
                    if (c instanceof AbstractXuYingCard) {
                        AbstractXuYingCard phantomCard = (AbstractXuYingCard) c;

                        AbstractMonster randomTarget = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                        if (randomTarget != null) {
                            phantomCard.queuePhantomAnimationAndEffect(randomTarget);
                        }
                    }
                }

                // 2. 如果还有剩余轮数，将【下一轮】的触发动作排在队列最末尾
                if (this.roundsLeft > 1) {
                    this.addToBot(new TriggerRoundAction(this.roundsLeft - 1));
                }
            }
            this.isDone = true;
        }
    }
}