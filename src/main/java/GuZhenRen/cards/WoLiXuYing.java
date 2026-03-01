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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WoLiXuYing extends AbstractXuYingCard {
    public static final String ID = GuZhenRen.makeID("WoLiXuYing");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = GuZhenRen.assetPath("img/cards/WoLiXuYing.png");

    private UUID previewTargetUUID = null;

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

    @Override
    public void applyPowers() {
        super.applyPowers();
        updatePreviewCard();
    }

    private void updatePreviewCard() {
        if (AbstractDungeon.isPlayerInDungeon() && AbstractDungeon.actionManager != null) {
            AbstractCard lastAttack = getValidLastAttack();
            if (lastAttack != null) {
                if (this.previewTargetUUID == null || !this.previewTargetUUID.equals(lastAttack.uuid)) {
                    this.cardsToPreview = lastAttack.makeStatEquivalentCopy();
                    this.previewTargetUUID = lastAttack.uuid;
                }
            } else {
                this.cardsToPreview = null;
                this.previewTargetUUID = null;
            }
        }
    }

    private AbstractCard getValidLastAttack() {
        for (int i = AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1; i >= 0; i--) {
            AbstractCard c = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(i);
            if (c.type == CardType.ATTACK && !(c instanceof AbstractXuYingCard) && !c.tags.contains(GuZhenRenTags.XU_YING_COPY)) {
                return c;
            }
        }
        return null;
    }

    // 动画逻辑
    @Override
    public void triggerPhantomEffect(AbstractMonster m) {
        AbstractCard lastAttack = getValidLastAttack();

        if (lastAttack != null && this.animatedPhantomCard != null) {
            final AbstractCard tmp = lastAttack.makeStatEquivalentCopy();
            final AbstractCard phantom = this.animatedPhantomCard;
            final AbstractMonster finalTarget = m;

            if (finalTarget != null && !finalTarget.isDeadOrEscaped()) {
                tmp.calculateCardDamage(finalTarget);
            }

            tmp.tags.add(GuZhenRenTags.XU_YING_COPY);
            tmp.purgeOnUse = true;
            tmp.energyOnUse = lastAttack.energyOnUse;

            // 1. 将复制的新牌放入悬浮层顶层，初始状态设为【极小且全透明】
            AbstractDungeon.player.limbo.addToTop(tmp);
            tmp.current_x = Settings.WIDTH / 2.0F;
            tmp.current_y = Settings.HEIGHT / 2.0F;
            tmp.target_x = Settings.WIDTH / 2.0F;
            tmp.target_y = Settings.HEIGHT / 2.0F;
            tmp.drawScale = 0.1F; // 初始极小
            tmp.targetDrawScale = 0.9F; // 目标正常大小
            tmp.transparency = 0.01F; // 初始近乎透明
            tmp.targetTransparency = 1.0F; // 目标完全清晰

            // addToTop 是插队逻辑，后压入的代码会最先执行

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

            // 1.最先执行的变身动画，拉长了变形时间，让淡入淡出更舒缓自然
            this.addToTop(new AbstractGameAction() {
                private boolean first = true;
                {
                    this.actionType = ActionType.WAIT;
                    this.duration = Settings.FAST_MODE ? 0.3F : 0.45F; // 动画时间拉长到 0.45 秒
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
        }
    }
}