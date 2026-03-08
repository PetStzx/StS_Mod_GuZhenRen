package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.util.ArrayList;
import java.util.List;

public class WoLiXuYing extends AbstractXuYingCard {
    public static final String ID = GuZhenRen.makeID("WoLiXuYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WoLiXuYing.png");

    // 用于存储当前随机选中的攻击牌
    private AbstractCard randomAttackCard = null;

    public WoLiXuYing() {
        super(ID, NAME, IMG_PATH, -2, DESCRIPTION,
                CardType.ATTACK,
                CardColorEnum.GUZHENREN_GREY,
                CardTarget.NONE);

        this.baseChanceFloat = 0.15f;
        this.initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.baseChanceFloat = 0.30f;
            this.initializeDescription();
        }
    }

    // =========================================================================
    // 从玩家真实的牌组 (Master Deck) 中随机抽取一张攻击牌
    // =========================================================================
    private void rollRandomAttack() {
        if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck != null) {
            ArrayList<AbstractCard> attacks = new ArrayList<>();
            // 遍历玩家的初始牌库
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                // 筛选攻击牌，同时排除虚影牌自己（防止无限套娃）
                if (c.type == CardType.ATTACK && !(c instanceof AbstractXuYingCard)) {
                    attacks.add(c);
                }
            }

            if (!attacks.isEmpty()) {
                // 随机抽取并克隆一个干净的副本
                AbstractCard picked = attacks.get(AbstractDungeon.cardRandomRng.random(attacks.size() - 1));
                this.randomAttackCard = picked.makeStatEquivalentCopy();
                this.cardsToPreview = this.randomAttackCard;
            } else {
                this.randomAttackCard = null;
                this.cardsToPreview = null;
            }
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();

        // 如果还没抽取过，或者进战斗第一回合，抽一张
        if (this.randomAttackCard == null && AbstractDungeon.isPlayerInDungeon()) {
            rollRandomAttack();
        }

        // 核心细节：让悬停展示的那张牌也应用一下力量等属性加成
        // 这样玩家就能直观地看到它打出去到底能造成多少伤害
        if (this.randomAttackCard != null && AbstractDungeon.player != null) {
            this.randomAttackCard.applyPowers();
        }
    }

    // =========================================================================
    // 动画与触发逻辑
    // =========================================================================
    @Override
    public void triggerPhantomEffect(AbstractMonster m) {
        if (this.randomAttackCard == null) {
            rollRandomAttack(); // 兜底保护
        }

        if (this.randomAttackCard != null && this.animatedPhantomCard != null) {
            final AbstractCard tmp = this.randomAttackCard.makeStatEquivalentCopy();
            final AbstractCard phantom = this.animatedPhantomCard;
            final AbstractMonster finalTarget = m;

            if (finalTarget != null && !finalTarget.isDeadOrEscaped()) {
                tmp.calculateCardDamage(finalTarget);
            }

            tmp.tags.add(GuZhenRenTags.XU_YING_COPY);
            tmp.purgeOnUse = true;
            // X 费卡牌会读取当前剩余能量
            tmp.energyOnUse = EnergyPanel.totalCount;

            AbstractDungeon.player.limbo.addToTop(tmp);
            tmp.current_x = Settings.WIDTH / 2.0F;
            tmp.current_y = Settings.HEIGHT / 2.0F;
            tmp.target_x = Settings.WIDTH / 2.0F;
            tmp.target_y = Settings.HEIGHT / 2.0F;
            tmp.drawScale = 0.1F;
            tmp.targetDrawScale = 0.9F;
            tmp.transparency = 0.01F;
            tmp.targetTransparency = 1.0F;

            // 5.攻击特效演示完，从屏幕上抹除这张克隆牌
            this.addToTop(new AbstractGameAction() {
                @Override
                public void update() {
                    if (AbstractDungeon.player.limbo.contains(tmp)) {
                        AbstractDungeon.player.limbo.removeCard(tmp);
                    }
                    this.isDone = true;
                }
            });

            // 4.攻击动作执行后，等待一会儿，让玩家看清特效和跳出的伤害数字
            this.addToTop(new com.megacrit.cardcrawl.actions.utility.WaitAction(Settings.FAST_MODE ? 0.2F : 0.3F));

            // 3.引发攻击动作
            this.addToTop(new AbstractGameAction() {
                @Override
                public void update() {
                    int startIndex = AbstractDungeon.actionManager.actions.size();
                    tmp.use(AbstractDungeon.player, finalTarget);
                    int endIndex = AbstractDungeon.actionManager.actions.size();

                    if (endIndex > startIndex) {
                        List<AbstractGameAction> stolenActions = new ArrayList<>();
                        for (int i = startIndex; i < endIndex; i++) {
                            stolenActions.add(AbstractDungeon.actionManager.actions.get(i));
                        }
                        for (int i = 0; i < stolenActions.size(); i++) {
                            AbstractDungeon.actionManager.actions.remove(AbstractDungeon.actionManager.actions.size() - 1);
                        }
                        for (int i = stolenActions.size() - 1; i >= 0; i--) {
                            AbstractDungeon.actionManager.addToTop(stolenActions.get(i));
                        }
                    }
                    this.isDone = true;
                }
            });

            // 2.变身完成后，定格一会儿
            this.addToTop(new com.megacrit.cardcrawl.actions.utility.WaitAction(Settings.FAST_MODE ? 0.15F : 0.25F));

            // 1.最先执行的变身动画
            this.addToTop(new AbstractGameAction() {
                private boolean first = true;
                {
                    this.actionType = ActionType.WAIT;
                    this.duration = Settings.FAST_MODE ? 0.3F : 0.45F;
                }
                @Override
                public void update() {
                    if (first) {
                        phantom.targetTransparency = 0.0F;
                        phantom.targetDrawScale = 0.1F;
                        first = false;
                    }
                    this.tickDuration();
                }
            });

            rollRandomAttack();
        }
    }
}