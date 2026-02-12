package GuZhenRen.cards;

import GuZhenRen.patches.GuZhenRenTags;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.badlogic.gdx.graphics.Color;

public abstract class AbstractBenMingGuCard extends AbstractGuZhenRenCard {

    // 静态开关：用于标记当前是否处于杀招组并阶段
    public static boolean isSynthesizing = false;

    public int maxRank = 9;

    public AbstractBenMingGuCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
        this.tags.add(GuZhenRenTags.BEN_MING_GU);
    }

    @Override
    public void onRemoveFromMasterDeck() {
        if (isSynthesizing) {
            return;
        }

        if (AbstractDungeon.isPlayerInDungeon() && AbstractDungeon.player != null) {
            int damageAmount = (int)(AbstractDungeon.player.maxHealth * 0.8F);
            if (damageAmount < 1) damageAmount = 1;

            if (AbstractDungeon.player.currentHealth <= damageAmount) {
                damageAmount = AbstractDungeon.player.currentHealth - 1;
            }

            if (damageAmount > 0) {
                CardCrawlGame.sound.play("BLUNT_HEAVY");
                AbstractDungeon.topLevelEffectsQueue.add(new BorderFlashEffect(Color.RED));
                AbstractDungeon.player.damage(new DamageInfo(null, damageAmount, DamageInfo.DamageType.HP_LOSS));
            }
        }
    }

    @Override
    public boolean canUpgrade() {
        return this.rank < this.maxRank;
    }

    @Override
    public void upgrade() {
        if (canUpgrade()) {
            this.upgradeRank(1);
            performUpgradeEffect();

            // 1. 调用 applyPowers 更新数值
            // 对于战斗中的手牌，这一步是必要的，因为我们要立刻显示力量加成后的伤害
            this.applyPowers();

            // 2. 清洗大师牌组数据
            // 如果这张牌是大师牌组里的“本体”，它不应该保留战斗中的临时Buff
            if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck.contains(this)) {
                // 重置为基础值
                this.damage = this.baseDamage;
                this.isDamageModified = false;

                this.block = this.baseBlock;
                this.isBlockModified = false;

                this.magicNumber = this.baseMagicNumber;
                this.isMagicNumberModified = false;
            }

            if (!this.upgraded) {
                this.upgraded = true;
                this.initializeTitle();
            }
        }
    }

    public abstract void performUpgradeEffect();
}