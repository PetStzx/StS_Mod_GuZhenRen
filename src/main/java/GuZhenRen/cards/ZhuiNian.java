package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.powers.NianPower;
import GuZhenRen.powers.QingPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ZhuiNian extends AbstractGuZhenRenCard {
    public static final String ID = GuZhenRen.makeID("ZhuiNian");
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/ZhuiNian.png");

    private static final int COST = 1;
    private static final int UPGRADE_COST = 0;
    private static final int INITIAL_RANK = 6;

    private static final int NIAN_MULTIPLIER = 3;
    private static final int PLAY_COUNT = 1;

    public ZhuiNian() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardColorEnum.GUZHENREN_GREY,
                CardRarity.RARE,
                CardTarget.SELF);

        this.setDao(Dao.ZHI_DAO);
        this.baseMagicNumber = this.magicNumber = PLAY_COUNT;
        this.baseSecondMagicNumber = this.secondMagicNumber = NIAN_MULTIPLIER;
        this.setRank(INITIAL_RANK);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ZhuiNianAction(this.magicNumber, this.secondMagicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(UPGRADE_COST);
            this.upgradeRank(1);
            this.initializeDescription();
        }
    }

    public static class ZhuiNianAction extends AbstractGameAction {
        private final int playCount;
        private final int nianMultiplier;

        public ZhuiNianAction(int playCount, int nianMultiplier) {
            this.playCount = playCount;
            this.nianMultiplier = nianMultiplier;
            this.actionType = ActionType.CARD_MANIPULATION;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            AbstractPlayer p = AbstractDungeon.player;

            if (this.duration == Settings.ACTION_DUR_FAST) {
                if (p.discardPile.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                String msg = ZhuiNian.cardStrings.EXTENDED_DESCRIPTION[0];
                AbstractDungeon.gridSelectScreen.open(p.discardPile, 1, msg, false);
                this.tickDuration();
                return;
            }

            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                AbstractDungeon.gridSelectScreen.selectedCards.clear();

                if (c != null) {
                    p.discardPile.group.remove(c);
                    AbstractDungeon.getCurrRoom().souls.remove(c);

                    p.limbo.group.add(c);
                    c.current_y = -200.0F * Settings.scale;
                    c.target_x = (float)Settings.WIDTH / 2.0F + 200.0F * Settings.scale;
                    c.target_y = (float)Settings.HEIGHT / 2.0F;
                    c.targetAngle = 0.0F;
                    c.lighten(false);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;

                    c.freeToPlayOnce = true;
                    c.exhaust = true;
                    c.applyPowers();

                    int costToCalc = Math.max(0, c.cost);
                    int baseNianGain = costToCalc * this.nianMultiplier;

                    int qingBonus = 0;
                    if (p.hasPower(QingPower.POWER_ID)) {
                        qingBonus = p.getPower(QingPower.POWER_ID).amount / 3;
                    }
                    int totalNianGain = baseNianGain > 0 ? (baseNianGain + qingBonus) : 0;

                    if (totalNianGain > 0) {
                        AbstractDungeon.actionManager.addToTop(new UnlimboAction(c));

                        for (int i = 0; i < this.playCount; i++) {
                            AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                            AbstractDungeon.actionManager.addToTop(new NewQueueCardAction(c, target, false, true));
                            if (i < this.playCount - 1) {
                                AbstractDungeon.actionManager.addToTop(new WaitAction(0.1F));
                            }
                        }
                        AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(p, p,
                                new NianPower(p, totalNianGain), totalNianGain));

                    } else {
                        AbstractDungeon.actionManager.addToTop(new UnlimboAction(c));
                        for (int i = 0; i < this.playCount; i++) {
                            AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                            AbstractDungeon.actionManager.addToTop(new NewQueueCardAction(c, target, false, true));
                        }
                    }
                }
            }
            this.isDone = true;
        }
    }
}