package GuZhenRen.powers;

import GuZhenRen.GuZhenRen;
import GuZhenRen.cards.AbstractXuYingCard;
import GuZhenRen.cards.JianYing;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class YiXinErYongPower extends AbstractPower {
    public static final String POWER_ID = GuZhenRen.makeID("YiXinErYongPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public YiXinErYongPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = true;

        String pathLarge = GuZhenRen.assetPath("img/powers/YiXinErYongPower_p.png");
        String pathSmall = GuZhenRen.assetPath("img/powers/YiXinErYongPower.png");

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathLarge), 0, 0, 88, 88);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(pathSmall), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!card.purgeOnUse && !card.isInAutoplay && this.amount > 0) {

            this.flash();
            this.amount--;

            if (this.amount == 0) {
                this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            }

            // 随机打牌逻辑
            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    ArrayList<AbstractCard> hand = AbstractDungeon.player.hand.group;
                    ArrayList<AbstractCard> candidates = new ArrayList<>();

                    for (AbstractCard c : hand) {
                        // 排除正在打出的牌，排除虚影牌，排除剑影
                        if (c != card && !(c instanceof AbstractXuYingCard) && !c.cardID.equals(JianYing.ID)) {
                            candidates.add(c);
                        }
                    }

                    if (!candidates.isEmpty()) {
                        // 随机选一张
                        AbstractCard randomCard = candidates.get(AbstractDungeon.cardRandomRng.random(candidates.size() - 1));

                        // 随机目标
                        AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);

                        // 让牌免费
                        randomCard.freeToPlayOnce = true;

                        // 打出
                        this.addToTop(new NewQueueCardAction(randomCard, target, true, true));
                    }
                    this.isDone = true;
                }
            });

            updateDescription();
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}