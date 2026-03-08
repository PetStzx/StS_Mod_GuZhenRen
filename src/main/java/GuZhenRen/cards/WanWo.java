package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class WanWo extends AbstractShaZhaoCard {
    public static final String ID = GuZhenRen.makeID("WanWo");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WanWo.png");

    private static final int COST = -1; // X费

    public WanWo() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL,
                CardTarget.NONE);

        this.setDao(Dao.LI_DAO);
        this.exhaust = true;

        this.cardsToPreview = new WoLiXuYing();
        this.cardsToPreview.upgrade();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new WanWoAction(p, this.freeToPlayOnce, this.energyOnUse, this.cardsToPreview));
    }


    public static class WanWoAction extends AbstractGameAction {
        private boolean freeToPlayOnce;
        private AbstractPlayer p;
        private int energyOnUse;
        private AbstractCard previewCard;

        public WanWoAction(AbstractPlayer p, boolean freeToPlayOnce, int energyOnUse, AbstractCard previewCard) {
            this.p = p;
            this.freeToPlayOnce = freeToPlayOnce;
            this.energyOnUse = energyOnUse;
            this.previewCard = previewCard;
        }

        @Override
        public void update() {
            int effect = EnergyPanel.totalCount;
            if (this.energyOnUse != -1) {
                effect = this.energyOnUse;
            }
            if (this.p.hasRelic("Chemical X")) {
                effect += 2;
                this.p.getRelic("Chemical X").flash();
            }

            if (effect > 0) {
                // 1. 给模板卡牌打上特有的新生标记 (99999)
                AbstractXuYingCard template = (AbstractXuYingCard) this.previewCard.makeStatEquivalentCopy();
                template.misc = 99999;

                // 2. 将生成卡牌的动作排入队列 (生成器会自动把 99999 标记克隆进手牌)
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(template, effect));

                // 3. 将扫描并触发新卡的动作排在生成动作的后面
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        // 遍历当前手牌
                        for (AbstractCard c : p.hand.group) {
                            // 认准标记 99999
                            if (c instanceof AbstractXuYingCard && c.misc == 99999) {
                                // 抹除标记，防止以后被重复识别
                                c.misc = 0;

                                AbstractMonster randomTarget = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                                if (randomTarget != null) {
                                    ((AbstractXuYingCard) c).queuePhantomAnimationAndEffect(randomTarget);
                                }
                            }
                        }
                        this.isDone = true;
                    }
                });

                if (!this.freeToPlayOnce) {
                    this.p.energy.use(EnergyPanel.totalCount);
                }
            }
            this.isDone = true;
        }
    }
}