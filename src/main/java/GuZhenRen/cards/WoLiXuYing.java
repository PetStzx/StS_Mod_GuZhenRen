package GuZhenRen.cards;

import GuZhenRen.GuZhenRen;
import GuZhenRen.actions.common.TriggerCardAction;import GuZhenRen.actions.common.CenterCardDisplayAction;import GuZhenRen.patches.CardColorEnum;
import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.actions.utility.WaitAction;import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import java.util.ArrayList;

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
            this.baseChanceFloat = 0.25f;
            this.initializeDescription();
        }
    }

    // =========================================================================
    // 从玩家真实的牌组中随机抽取一张非消耗攻击牌
    // =========================================================================
    private void rollRandomAttack() {
        if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck != null) {
            ArrayList<AbstractCard> attacks = new ArrayList<>();
            // 遍历玩家的初始牌库
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                // 筛选攻击牌，排除虚影牌自己，排除消耗牌
                if (c.type == CardType.ATTACK && !(c instanceof AbstractXuYingCard) && !c.exhaust) {
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

        // 让悬停展示的牌也应用力量等属性加成
        if (this.randomAttackCard != null && AbstractDungeon.player != null) {
            this.randomAttackCard.applyPowers();

            // 抵消预览卡牌的钢笔尖虚高伤害
            if (AbstractDungeon.player.hasPower("Pen Nib")) {
                this.randomAttackCard.damage /= 2;
                this.randomAttackCard.isDamageModified = (this.randomAttackCard.damage != this.randomAttackCard.baseDamage);
            }
        }
    }

    @Override
    public void triggerPhantomEffect(AbstractMonster m) {
        if (this.randomAttackCard == null) {
            rollRandomAttack();
        }

        if (this.randomAttackCard != null && this.animatedPhantomCard != null) {
            AbstractCard tmp = prepareCloneCard(m);
            this.addToTop(new CenterCardDisplayAction(tmp, null, CenterCardDisplayAction.Phase.CLEAR));
            this.addToTop(new WaitAction(Settings.FAST_MODE ? 0.2F : 0.3F));
            this.addToTop(new TriggerCardAction(tmp, m));
            this.addToTop(new WaitAction(Settings.FAST_MODE ? 0.15F : 0.25F));
            this.addToTop(new CenterCardDisplayAction(tmp, this.animatedPhantomCard, CenterCardDisplayAction.Phase.SETUP));
            rollRandomAttack();
        }
    }

    private AbstractCard prepareCloneCard(AbstractMonster m) {
        AbstractCard tmp = this.randomAttackCard.makeStatEquivalentCopy();
        if (m != null && !m.isDeadOrEscaped()) {
            tmp.calculateCardDamage(m);
        } else {
            tmp.applyPowers();
        }

        // 抵消钢笔尖
        if (AbstractDungeon.player.hasPower("Pen Nib")) {
            tmp.damage /= 2;
            tmp.isDamageModified = (tmp.damage != tmp.baseDamage);
        }

        tmp.tags.add(GuZhenRenTags.XU_YING_COPY);
        tmp.purgeOnUse = true;
        tmp.energyOnUse = EnergyPanel.totalCount; // 保证X费牌读取真实能量
        tmp.freeToPlayOnce = true;

        return tmp;
    }
}